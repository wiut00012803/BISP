from django.contrib import admin
from django.urls import path, include
from accounts.api_views import api_register, api_login

urlpatterns = [
    path('admin/', admin.site.urls),
    path('accounts/', include('accounts.urls')),
    path('', include('external_systems.urls')),
    path('api/', include('external_systems.api_urls')),
    path('api/register/', api_register, name='api_register'),
    path('api/login/', api_login, name='api_login'),
]
