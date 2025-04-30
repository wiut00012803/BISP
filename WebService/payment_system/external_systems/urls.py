from django.urls import path
from external_systems import views

app_name = 'external_systems'

urlpatterns = [
    path('', views.dashboard, name='dashboard'),
    path('profile/', views.profile_view, name='profile'),
    path('history/', views.history_view, name='history'),
]
