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
    user = models.ForeignKey('accounts.User', on_delete=models.CASCADE)
    platform = models.CharField(max_length=50, choices=PLATFORM_CHOICES)
    card_number = models.CharField(max_length=16, unique=True, default="0000000000000000")
    card_type = models.CharField(max_length=10, choices=CARD_TYPE_CHOICES, default='Uzcard')
    bank = models.CharField(max_length=20, choices=BANK_CHOICES, default='TBC bank')
    balance = models.DecimalField(max_digits=10, decimal_places=2, default=1000.00)
    cashback = models.DecimalField(max_digits=10, decimal_places=2, default=0.00)
    commission = models.DecimalField(max_digits=5, decimal_places=2, default=2.50)

    def save(self, *args, **kwargs):
        if not self.pk and self.card_number == "0000000000000000":
            qs = PaymentCard.objects.all()
            max_val = qs.aggregate(Max('card_number'))['card_number__max']
            if max_val is None:
                self.card_number = "0000000000000000"
            else:
                self.card_number = str(int(max_val) + 1).zfill(16)
        super().save(*args, **kwargs)
