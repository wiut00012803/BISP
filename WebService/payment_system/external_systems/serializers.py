from rest_framework import serializers

from .models import PaymentCard, Transaction
from .models import Location, OnSiteTransaction


class PaymentCardSerializer(serializers.ModelSerializer):
    class Meta:
        model = PaymentCard
        fields = ['id', 'platform', 'card_number', 'card_type', 'bank', 'balance', 'cashback', 'commission']


class TransactionSerializer(serializers.ModelSerializer):
    source_card_number = serializers.CharField(source='source_card.card_number')
    destination_card_number = serializers.CharField(source='destination_card.card_number')

    class Meta:
        model = Transaction
        fields = ['id', 'source_card_number', 'destination_card_number', 'amount', 'fee', 'timestamp']


class TransferSerializer(serializers.Serializer):
    source_card_number = serializers.CharField(max_length=16)
    destination_card_number = serializers.CharField(max_length=16)
    amount = serializers.DecimalField(max_digits=10, decimal_places=2)

class LocationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Location
        fields = ['id', 'name']

class OnSiteTransactionSerializer(serializers.ModelSerializer):
    location = serializers.CharField(source='location.name')
    platform = serializers.CharField(source='platform.name')
    card_number = serializers.CharField(source='card.card_number')

    class Meta:
        model = OnSiteTransaction
        fields = ['id', 'card_number', 'platform', 'location', 'amount', 'cashback_amount', 'timestamp']
