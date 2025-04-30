import random
from decimal import Decimal

from django.shortcuts import render, redirect
from external_systems.serializers import PaymentCardSerializer
from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView

from .forms import DestinationForm
from .models import PaymentCard, Transaction, Platform, Location, PlatformLocation, OnSiteTransaction
from .serializers import LocationSerializer, OnSiteTransactionSerializer, PaymentCardSerializer, TransferSerializer, \
    TransactionSerializer


class ProfileApi(APIView):
    def get(self, request):
        cards = request.user.paymentcard_set.all()
        return Response(PaymentCardSerializer(cards, many=True).data)


def profile_view(request):
    if not request.user.is_authenticated:
        return redirect('login')
    cards = PaymentCard.objects.filter(user=request.user)
    return render(request, 'profile.html',
                  {'full_cards': cards})


def history_view(request):
    if not request.user.is_authenticated:
        return redirect('login')
    txs = Transaction.objects.filter(user=request.user).order_by('-timestamp')
    onsite_tx = OnSiteTransaction.objects.filter(user=request.user).order_by('-timestamp')
    return render(request, 'history.html',
                  {'history': txs, 'pay_history': onsite_tx})


class SourceOptions(APIView):
    def get(self, request):
        dst = request.GET.get("dest_card_number", "")
        if len(dst) != 16:
            return Response([])
        try:
            dest = PaymentCard.objects.get(card_number=dst, user=request.user)
        except PaymentCard.DoesNotExist:
            return Response([])
        cards = PaymentCard.objects.filter(user=request.user)
        allowed = []
        for c in cards:
            if c.card_number == dst:
                continue
            if c.card_type == "Visa" and dest.card_type in ["Uzcard", "Humo"]:
                continue
            allowed.append(c)
        allowed.sort(key=lambda c: (c.commission, c.cashback))
        data = [{"card_number": c.card_number,
                 "label": f"{c.card_type} ({c.bank}) – {c.card_number} – comm {c.commission}% – cb {c.cashback}"} for c
                in
                allowed]
        return Response(data)


def ensure_dummy_cards(user):
    if PaymentCard.objects.filter(user=user).exists():
        return
    platforms = ["Dummy Platform 1", "Dummy Platform 2", "Dummy Platform 3"]
    for p in platforms:
        for ctype, bank in [("Uzcard", "Anorbank"), ("Humo", "Anorbank"), ("Visa", "TBC bank")]:
            PaymentCard.objects.create(user=user, platform=p, card_number=''.join(random.choices("0123456789", k=16)),
                                       card_type=ctype, bank=bank)


def get_source_cards(user, dst_card):
    qs = PaymentCard.objects.filter(user=user).exclude(card_number=dst_card.card_number)
    if dst_card.card_type == "Visa":
        qs = qs.filter(card_type="Visa")
    else:
        qs = qs.exclude(card_type="Visa")
    return sorted(qs, key=lambda c: (0 if c.bank == dst_card.bank else 1, c.commission, c.cashback))


def dashboard(request):
    if not request.user.is_authenticated:
        return redirect("login")
    ensure_dummy_cards(request.user)
    full_cards = PaymentCard.objects.filter(user=request.user)
    pay_history = OnSiteTransaction.objects.filter(user=request.user).order_by('-timestamp')
    history = Transaction.objects.filter(user=request.user).order_by('-timestamp')
    dest_form = DestinationForm()
    dest_selected = False
    source_cards = []
    message = ""

    if request.method == "POST":
        action = request.POST.get("action")
        if action == "continue":
            dest_form = DestinationForm(request.POST)
            if dest_form.is_valid():
                dst_num = dest_form.cleaned_data["destination_card_number"]
                try:
                    dst_card = PaymentCard.objects.get(card_number=dst_num, user=request.user)
                    dest_selected = True
                    source_cards = get_source_cards(request.user, dst_card)
                except PaymentCard.DoesNotExist:
                    dest_form.add_error("destination_card_number", "Card not found")
        elif action == "transfer":
            dst_num = request.POST.get("destination_card_number")
            src_num = request.POST.get("source_card_number")
            try:
                amt = Decimal(request.POST.get("amount"))
            except:
                amt = Decimal("0")
            try:
                dst_card = PaymentCard.objects.get(card_number=dst_num, user=request.user)
                src_card = PaymentCard.objects.get(card_number=src_num, user=request.user)
                dest_selected = True
                source_cards = get_source_cards(request.user, dst_card)
                if src_card.card_number == dst_card.card_number:
                    message = "Source and destination must differ"
                elif src_card.card_type == "Visa" and dst_card.card_type in ["Uzcard", "Humo"]:
                    message = "Visa→Uzcard/Humo not allowed"
                else:
                    fee = Decimal("0")
                    if not (src_card.bank == dst_card.bank and src_card.card_type in ["Uzcard",
                                                                                      "Humo"] and dst_card.card_type in [
                                "Uzcard", "Humo"]):
                        fee = amt * src_card.commission / Decimal("100")
                    if src_card.balance < amt + fee:
                        message = "Insufficient funds"
                    else:
                        src_card.balance -= amt + fee
                        dst_card.balance += amt
                        src_card.cashback += fee
                        src_card.save()
                        dst_card.save()
                        Transaction.objects.create(user=request.user, source_card=src_card, destination_card=dst_card,
                                                   amount=amt, fee=fee)
                        message = "Transfer successful"
            except PaymentCard.DoesNotExist:
                message = "Card not found"
            dest_form = DestinationForm({"destination_card_number": dst_num})

    return render(request, "dashboard.html",
                  {"full_cards": full_cards, "history": history, "dest_form": dest_form, "dest_selected": dest_selected,
                   "source_cards": source_cards, "message": message, "pay_history": pay_history, })


class PaymentCardList(APIView):
    def get(self, request):
        cards = PaymentCard.objects.filter(user=request.user)
        if not cards.exists():
            for platform in ['Dummy Platform 1', 'Dummy Platform 2', 'Dummy Platform 3']:
                PaymentCard.objects.create(user=request.user, platform=platform, card_name=platform)
            cards = PaymentCard.objects.filter(user=request.user)
        serializer = PaymentCardSerializer(cards, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)


class TransferMoney(APIView):
    def post(self, request):
        serializer = TransferSerializer(data=request.data)
        if serializer.is_valid():
            source_cn = serializer.validated_data["source_card_number"]
            destination_cn = serializer.validated_data["destination_card_number"]
            amount = serializer.validated_data["amount"]
            if source_cn == destination_cn:
                return Response({"error": "Source and destination cannot be the same"},
                                status=status.HTTP_400_BAD_REQUEST)
            try:
                source_card = PaymentCard.objects.get(card_number=source_cn, user=request.user)
                destination_card = PaymentCard.objects.get(card_number=destination_cn, user=request.user)
            except PaymentCard.DoesNotExist:
                return Response({"error": "Card not found"}, status=status.HTTP_404_NOT_FOUND)
            if source_card.card_type == "Visa" and destination_card.card_type in ["Uzcard", "Humo"]:
                return Response({"error": "Transfer not allowed from Visa to Uzcard or Humo"},
                                status=status.HTTP_400_BAD_REQUEST)
            if source_card.card_type in ["Uzcard", "Humo"] and destination_card.card_type in ["Uzcard",
                                                                                              "Humo"] and source_card.bank == "Anorbank" and destination_card.bank == "Anorbank":
                fee = Decimal("0")
            else:
                fee = amount * source_card.commission / Decimal("100")
            if source_card.balance < amount + fee:
                return Response({'error': 'Insufficient funds'}, status=status.HTTP_400_BAD_REQUEST)
            source_card.balance -= amount + fee
            destination_card.balance += amount
            source_card.cashback += fee
            source_card.save()
            destination_card.save()
            Transaction.objects.create(
                user=request.user,
                source_card=source_card,
                destination_card=destination_card,
                amount=amount,
                fee=fee
            )
            return Response({
                'source': PaymentCardSerializer(source_card).data,
                'destination': PaymentCardSerializer(destination_card).data
            }, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class TransactionList(APIView):
    def get(self, request):
        txs = Transaction.objects.filter(user=request.user).order_by('-timestamp')
        ser = TransactionSerializer(txs, many=True)
        return Response(ser.data, status=status.HTTP_200_OK)


class LocationList(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        q = request.GET.get('q', '')
        qs = Location.objects.filter(name__icontains=q)
        ser = LocationSerializer(qs, many=True)
        return Response(ser.data, status=status.HTTP_200_OK)


class OnSitePay(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        card_num = request.data.get('card_number', '')
        loc_id = request.data.get('location_id')
        amt = request.data.get('amount')
        try:
            amount = Decimal(str(amt))
        except:
            return Response({'error': 'Invalid amount'}, status=status.HTTP_400_BAD_REQUEST)
        try:
            card = PaymentCard.objects.get(card_number=card_num, user=request.user)
            plat = Platform.objects.get(name=card.platform)
            location = Location.objects.get(pk=loc_id)
            rule = PlatformLocation.objects.get(platform=plat, location=location)
        except PaymentCard.DoesNotExist:
            return Response({'error': 'Card not found'}, status=status.HTTP_404_NOT_FOUND)
        except PlatformLocation.DoesNotExist:
            return Response({'error': 'No cashback rule for this platform/location'},
                            status=status.HTTP_400_BAD_REQUEST)
        if card.balance < amount:
            return Response({'error': 'Insufficient funds on card'}, status=status.HTTP_400_BAD_REQUEST)
        # debit card
        card.balance -= amount
        card.save()
        # compute and credit platform cashback
        cashback_amt = (amount * rule.cashback_percent) / Decimal('100')
        plat.cashback_balance += cashback_amt
        plat.save()
        # record transaction
        tx = OnSiteTransaction.objects.create(
            user=request.user,
            card=card,
            platform=plat,
            location=location,
            amount=amount,
            cashback_amount=cashback_amt
        )
        ser = OnSiteTransactionSerializer(tx)
        return Response(ser.data, status=status.HTTP_201_CREATED)


class OnSiteHistory(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        txs = OnSiteTransaction.objects.filter(user=request.user).order_by('-timestamp')
        ser = OnSiteTransactionSerializer(txs, many=True)
        return Response(ser.data, status=status.HTTP_200_OK)
