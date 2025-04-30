from django.conf import settings
from django.db import models, connection
from django.db.models import Max


def get_next_card_number():
    if 'external_systems_paymentcard' not in connection.introspection.table_names():
        return '0000000000000000'
    max_number = PaymentCard.objects.aggregate(Max('card_number'))['card_number__max']
    if max_number is None:
        return '0000000000000000'
    return str(int(max_number) + 1).zfill(16)


class PaymentCard(models.Model):
    PLATFORM_CHOICES = [
        ('Dummy Platform 1', 'Dummy Platform 1'),
        ('Dummy Platform 2', 'Dummy Platform 2'),
        ('Dummy Platform 3', 'Dummy Platform 3'),
    ]
    CARD_TYPE_CHOICES = [
        ('Uzcard', 'Uzcard'),
        ('Humo', 'Humo'),
        ('Visa', 'Visa'),
    ]
    BANK_CHOICES = [
        ('ipotekabank', 'ipotekabank'),
        ('uzum', 'uzum'),
        ('Anorbank', 'Anorbank'),
        ('TBC bank', 'TBC bank'),
        ('Kapitalbank', 'Kapitalbank'),
    ]
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    platform = models.CharField(max_length=50, choices=PLATFORM_CHOICES)
    card_number = models.CharField(max_length=16, unique=True)
    card_type = models.CharField(max_length=10, choices=CARD_TYPE_CHOICES)
    bank = models.CharField(max_length=20, choices=BANK_CHOICES)
    balance = models.DecimalField(max_digits=10, decimal_places=2, default=1000.00)
    cashback = models.DecimalField(max_digits=10, decimal_places=2, default=0.00)
    commission = models.DecimalField(max_digits=5, decimal_places=2, default=2.50)


class Transaction(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    source_card = models.ForeignKey(PaymentCard, related_name='tx_source', on_delete=models.CASCADE)
    destination_card = models.ForeignKey(PaymentCard, related_name='tx_dest', on_delete=models.CASCADE)
    amount = models.DecimalField(max_digits=10, decimal_places=2)
    fee = models.DecimalField(max_digits=10, decimal_places=2)
    timestamp = models.DateTimeField(auto_now_add=True)


class Platform(models.Model):
    name = models.CharField(max_length=50, unique=True)
    cashback_balance = models.DecimalField(max_digits=12, decimal_places=2, default=0)


class Location(models.Model):
    name = models.CharField(max_length=100, unique=True)


class PlatformLocation(models.Model):
    platform = models.ForeignKey(Platform, on_delete=models.CASCADE)
    location = models.ForeignKey(Location, on_delete=models.CASCADE)
    cashback_percent = models.DecimalField(max_digits=5, decimal_places=2)

    class Meta:
        unique_together = ('platform', 'location')


class OnSiteTransaction(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    card = models.ForeignKey(PaymentCard, on_delete=models.CASCADE)
    platform = models.ForeignKey(Platform, on_delete=models.CASCADE)
    location = models.ForeignKey(Location, on_delete=models.CASCADE)
    amount = models.DecimalField(max_digits=10, decimal_places=2)
    cashback_amount = models.DecimalField(max_digits=10, decimal_places=2)
    timestamp = models.DateTimeField(auto_now_add=True)
