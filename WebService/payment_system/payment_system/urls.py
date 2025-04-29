from django.contrib import admin
from django.urls import path, include
from external_systems.views import dashboard

urlpatterns = [
    path('admin/', admin.site.urls),
    path('accounts/', include('accounts.urls')),
    path('api/', include('external_systems.api_urls')),
    path('', dashboard, name='dashboard'),
    path('api/accounts/', include('accounts.api_urls')),
]
