from django.urls import path
from .api_views import api_register

urlpatterns = [
    path('register/', api_register, name='api_register'),
]