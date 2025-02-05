import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect

class HomeView(View):
    def get(self, request, *args, **kwargs):
        if 'usuario' not in request.session:
            return redirect('login')
        
        user_data = {
            'usuario': request.session.get('usuario'),
            'rol': request.session.get('rol'),
        }
        return render(request, 'home.html', user_data)