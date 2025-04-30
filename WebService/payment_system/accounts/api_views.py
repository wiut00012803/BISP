from accounts.forms import RegistrationForm
from django.contrib.auth import authenticate
from django.contrib.auth import get_user_model
from django.contrib.auth.tokens import default_token_generator
from django.core.mail import send_mail
from django.utils.encoding import force_bytes
from django.utils.http import urlsafe_base64_encode
from rest_framework import status
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny
from rest_framework.response import Response

User = get_user_model()


@api_view(['POST'])
@permission_classes([AllowAny])
def api_register(request):
    form = RegistrationForm(request.data)
    if not form.is_valid():
        return Response(form.errors, status=status.HTTP_400_BAD_REQUEST)
    user = User.objects.create_user(
        email=form.cleaned_data['email'],
        password=form.cleaned_data['password']
    )
    uid = urlsafe_base64_encode(force_bytes(user.pk))
    token = default_token_generator.make_token(user)
    link = f"http://{request.get_host()}/accounts/activate/{uid}/{token}/"
    send_mail('Activate your account', link, 'noreply@example.com', [user.email])
    return Response({'detail': 'Registered – check email'}, status=status.HTTP_201_CREATED)


@api_view(['POST'])
@permission_classes([AllowAny])
def api_login(request):
    # read JSON or form fields
    username = request.data.get('username')
    password = request.data.get('password')
    if not username or not password:
        return Response(
            {'detail': 'Both username and password are required'},
            status=status.HTTP_400_BAD_REQUEST
        )
    user = authenticate(request, username=username, password=password)
    if not user:
        return Response(
            {'detail': 'Invalid credentials'},
            status=status.HTTP_400_BAD_REQUEST
        )
    token, created = Token.objects.get_or_create(user=user)
    return Response({'token': token.key})
