from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response
from django.contrib.sites.shortcuts import get_current_site
from django.utils.http import urlsafe_base64_encode
from django.utils.encoding import force_bytes
from django.contrib.auth.tokens import default_token_generator
from django.core.mail import send_mail
from .models import User
from .forms import RegistrationForm

@api_view(['POST'])
@permission_classes([AllowAny])
def api_register(request):
    form = RegistrationForm(request.data)
    if not form.is_valid():
        return Response(form.errors, status=400)
    user = User.objects.create_user(
        email=form.cleaned_data['email'],
        password=form.cleaned_data['password']
    )
    current_site = get_current_site(request)
    uid = urlsafe_base64_encode(force_bytes(user.pk))
    token = default_token_generator.make_token(user)
    link = f"http://{current_site.domain}/accounts/activate/{uid}/{token}/"
    send_mail('Activate your account', link,
              'noreply@example.com', [user.email])
    return Response({'detail':'Registration successful, check your email.'}, status=201)