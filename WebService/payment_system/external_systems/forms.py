from django import forms

class DestinationForm(forms.Form):
    destination_card_number = forms.CharField(
        max_length=16,
        label="Enter Card Number"
    )