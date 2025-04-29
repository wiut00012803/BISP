from django.urls import path
from . import views
from external_systems.views import dashboard

urlpatterns = [
    path('register/', views.registration, name='registration'),
    path('activate/<uidb64>/<token>/', views.activate, name='activation'),
    path('login/', views.login_view, name='login'),
    path('logout/', views.logout_view, name='logout'),
    path("", dashboard, name="dashboard"),
]