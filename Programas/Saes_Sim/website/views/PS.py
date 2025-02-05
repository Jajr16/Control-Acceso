import requests
from django.http import JsonResponse
from django.views import View
from django.shortcuts import render, redirect
from .url import url

class PSView(View):
    def get(self, request, *args, **kwargs):
        api = "ps"
        
        headers = {"Content-Type": "application/json"}
        
        response = requests.get(url+api, headers=headers)
        
        context = {
            "DetailPS": response.json()
        }
        
        return render(request, 'PS.html', context)