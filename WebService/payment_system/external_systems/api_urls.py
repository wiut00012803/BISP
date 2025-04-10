from django.urls import path
from . import views
from rest_framework.authtoken.views import obtain_auth_token

urlpatterns = [
    path('login/', obtain_auth_token, name='api_login'),
    path('cards/', views.PaymentCardList.as_view(), name='card_list'),
    path('transfer/', views.TransferMoney.as_view(), name='transfer'),
]