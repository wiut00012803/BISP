from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

from .views import (
    PaymentCardList,
    SourceOptions,
    TransferMoney,
)

urlpatterns = [
    path('login/', obtain_auth_token, name='api_login'),
    path('cards/', PaymentCardList.as_view(), name='api_cards'),
    path('source-options/', SourceOptions.as_view(), name='api_source_options'),
    path('transfer/', TransferMoney.as_view(), name='api_transfer'),
]
