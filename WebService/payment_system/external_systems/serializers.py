from rest_framework import serializers
from .models import PaymentCard

class PaymentCardSerializer(serializers.ModelSerializer):
    class Meta:
        model = PaymentCard
        fields = ['id', 'platform', 'card_name', 'balance', 'cashback', 'commission']

class TransferSerializer(serializers.Serializer):
    source_id = serializers.IntegerField()
    destination_id = serializers.IntegerField()
    amount = serializers.DecimalField(max_digits=10, decimal_places=2)