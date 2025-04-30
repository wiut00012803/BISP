from django.contrib import admin
from django.contrib.auth import get_user_model
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.contrib.auth.tokens import default_token_generator

User = get_user_model()

@admin.register(User)
class UserAdmin(BaseUserAdmin):
    ordering = ('email',)
    list_display = (
        'email',
        'is_active',
        'is_staff',
        'is_superuser',
        'activation_token',
    )
    readonly_fields = ('activation_token',)

    fieldsets = (
        (None, {
            'fields': (
                'email',
                'password',
                'activation_token',
            )
        }),
        ('Permissions', {
            'fields': (
                'is_active',
                'is_staff',
                'is_superuser',
                'groups',
                'user_permissions',
            )
        }),
        ('Important dates', {
            'fields': ('last_login',),
        }),
    )

    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('email', 'password1', 'password2'),
        }),
    )

    def activation_token(self, obj):
        return default_token_generator.make_token(obj)
    activation_token.short_description = 'Activation Token'