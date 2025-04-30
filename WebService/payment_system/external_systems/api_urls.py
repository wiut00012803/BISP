from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

from .views import (PaymentCardList, SourceOptions, TransferMoney, TransactionList, LocationList, OnSitePay,
                    OnSiteHistory, )

urlpatterns = [path('login/', obtain_auth_token, name='api_login'),
    path('cards/', PaymentCardList.as_view(), name='api_cards'),
    path('source-options/', SourceOptions.as_view(), name='api_source_options'),
    path('transfer/', TransferMoney.as_view(), name='api_transfer'),
    path('transactions/', TransactionList.as_view(), name='transactions'),
    path('locations/', LocationList.as_view(), name='api_locations'),
    path('pay/', OnSitePay.as_view(), name='api_on_site_pay'),
    path('pay/history/', OnSiteHistory.as_view(), name='api_on_site_history'), ]
