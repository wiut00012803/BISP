from django.urls import path
from external_systems.views import (
    PaymentCardList, TransferMoney,
    LocationList, OnSitePay, OnSiteHistory,
    TransactionList, ProfileApi, SourceOptions
)
urlpatterns = [
    path('source-options/', SourceOptions.as_view(), name='api_source_options'),
    path('cards/', PaymentCardList.as_view(), name='api_cards'),
    path('transfer/', TransferMoney.as_view(), name='api_transfer'),
    path('locations/', LocationList.as_view(), name='api_locations'),
    path('pay/', OnSitePay.as_view(), name='api_onsite'),
    path('pay/history/', OnSiteHistory.as_view(), name='api_onsite_history'),
    path('transactions/', TransactionList.as_view(), name='api_transactions'),
    path('profile/', ProfileApi.as_view(), name='api_profile'),
]
