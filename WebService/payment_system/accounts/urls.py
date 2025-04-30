from accounts import views
from accounts.api_views import api_register
from django.urls import path
from rest_framework.authtoken.views import obtain_auth_token

urlpatterns = [
    path('register/', views.registration, name='register'),
    path('activate/<uidb64>/<token>/', views.activate, name='activation'),
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout_view, name='logout'),
    path('api/register/', api_register, name='api_register'),
    path('api/login/', obtain_auth_token, name='api_login'),
]
