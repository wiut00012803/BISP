from django import forms
from .models import User

class RegistrationForm(forms.ModelForm):
    password = forms.CharField(widget=forms.PasswordInput)
    confirm_password = forms.CharField(widget=forms.PasswordInput)
    class Meta:
        model = User
        fields = ['email']
    def clean(self):
        data = super().clean()
        if data.get('password') != data.get('confirm_password'):
            raise forms.ValidationError('Passwords do not match')
        return data