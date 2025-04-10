from django.conf import settings
from django.db import models


class PaymentCard(models.Model):
    PLATFORM_CHOICES = [
        ('Dummy Platform 1', 'Dummy Platform 1'),
        ('Dummy Platform 2', 'Dummy Platform 2'),
        ('Dummy Platform 3', 'Dummy Platform 3'),
    ]
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    platform = models.CharField(max_length=50, choices=PLATFORM_CHOICES)
    card_name = models.CharField(max_length=100)
    balance = models.DecimalField(max_digits=10, decimal_places=2, default=1000.00)
    cashback = models.DecimalField(max_digits=10, decimal_places=2, default=0.00)
    commission = models.DecimalField(max_digits=5, decimal_places=2, default=2.50)
