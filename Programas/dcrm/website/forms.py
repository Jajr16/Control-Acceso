from django import forms
from captcha.fields import CaptchaField
from .models import Usuario

class LoginForm(forms.Form):
    boleta = forms.CharField(max_length=100, label="Boleta")
    contraseña = forms.CharField(widget=forms.PasswordInput)
    captcha = CaptchaField()
    
    def __init__(self, *args, **kwargs):
        super(LoginForm, self).__init__(*args, **kwargs)
        
        self.fields['boleta'].widget.attrs.update({
            'class': 'textEntry'
        })
        self.fields['contraseña'].widget.attrs.update({
            'class': 'passwordEntry'
        })
        
        self.fields['captcha'].widget.attrs.update({
            'class': 'textEntry', 
            'id': 'captcha-id'
        })

class RegistroForm(forms.ModelForm):
    password = forms.CharField(widget=forms.PasswordInput)

    class Meta:
        model = Usuario
        fields = ['boleta', 'nombre', 'apellido', 'password']
