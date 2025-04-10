from django.urls import path
from . import views

urlpatterns = [
    path('register/', views.registration, name='registration'),
    path('activate/<uidb64>/<token>/', views.activate, name='activation'),
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout_view, name='logout'),
]