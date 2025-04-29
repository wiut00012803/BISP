from .models import PaymentCard
from rest_framework import serializers


class PaymentCardSerializer(serializers.ModelSerializer):
    class Meta:
        model = PaymentCard
        fields = ["id", "platform", "card_number", "card_type", "bank", "balance", "cashback", "commission"]


class TransferSerializer(serializers.Serializer):
    source_card_number = serializers.CharField(max_length=16)
    destination_card_number = serializers.CharField(max_length=16)
    amount = serializers.DecimalField(max_digits=10, decimal_places=2)
