from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .models import PaymentCard
from .serializers import PaymentCardSerializer, TransferSerializer
from decimal import Decimal

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
            source_id = serializer.validated_data['source_id']
            destination_id = serializer.validated_data['destination_id']
            amount = serializer.validated_data['amount']
            try:
                source_card = PaymentCard.objects.get(id=source_id, user=request.user)
                destination_card = PaymentCard.objects.get(id=destination_id, user=request.user)
            except PaymentCard.DoesNotExist:
                return Response({'error': 'Card not found'}, status=status.HTTP_404_NOT_FOUND)
            fee = amount * source_card.commission / Decimal('100')
            total_deduction = amount + fee
            if source_card.balance < total_deduction:
                return Response({'error': 'Insufficient funds'}, status=status.HTTP_400_BAD_REQUEST)
            source_card.balance -= total_deduction
            destination_card.balance += amount
            source_card.cashback += fee
            source_card.save()
            destination_card.save()
            return Response({'source': PaymentCardSerializer(source_card).data, 'destination': PaymentCardSerializer(destination_card).data}, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)