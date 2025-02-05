from django import forms
from captcha.fields import CaptchaField
from django.forms.widgets import DateInput
from datetime import date
from django.core.exceptions import ValidationError
from .views.url import url
import requests

class LoginForm(forms.Form):
    boleta = forms.CharField(max_length=100, label="Boleta", required=True)
    contraseña = forms.CharField(widget=forms.PasswordInput, required=True)
    captcha = CaptchaField()
    
    def __init__(self, *args, **kwargs):
        super(LoginForm, self).__init__(*args, **kwargs)
        
        self.fields['boleta'].widget.attrs.update({
            'class': 'textEntry',
            'style': 'width: 6rem; height: 0.5rem; border: 1px solid black;',
        })
        self.fields['contraseña'].widget.attrs.update({
            'class': 'passwordEntry',
            'style': 'width: 6rem; height: 0.5rem; border: 1px solid black;',
        })
        
        self.fields['captcha'].widget.attrs.update({
            'class': 'textEntry', 
            'id': 'captcha-id',
            'style': 'width: 6rem; height: 0.5rem; border: 1px solid black;',
        })
        
class periodoForm(forms.Form):
    OPCIONES = [
        ('', 'Seleccione el tipo de examen'),
        ('O', 'Ordinario'),
        ('E', 'Especial')
    ]
    
    periodo = forms.CharField(max_length=20, required=True)
    tipo = forms.ChoiceField(choices=OPCIONES, required=True)
    fecha_I = forms.DateField(
        widget=DateInput(attrs={'type': 'date', 
                                'class': 'form-control', 
                                'min': date.today().strftime('%Y-%m-%d'),
                                'id': 'fecha_fin'}), 
        input_formats=['%Y-%m-%d'],
        required=True)
    
    fecha_F = forms.DateField(
        widget=DateInput(attrs={'type': 'date', 
                                'class': 'form-control',
                                'min': date.today().strftime('%Y-%m-%d'),
                                'id': 'fecha_fin'}), 
        input_formats=['%Y-%m-%d'],
        required=True)
    
    def clean(self):
        cleaned_data = super().clean()
        fecha_inicio = cleaned_data.get('fecha_I')
        fecha_fin = cleaned_data.get('fecha_F')

        if fecha_inicio and fecha_fin and fecha_fin < fecha_inicio:
            raise ValidationError("La fecha de fin no puede ser anterior a la fecha de inicio.")

        return cleaned_data
    
    def __init__(self, *args, **kwargs):
        super(periodoForm, self).__init__(*args, **kwargs)
        

class ETSForm(forms.Form):
    @staticmethod
    def obtener_opciones(api_endpoint, id_key, label_key):
        response = requests.get(url + api_endpoint)
        if response.status_code == 200:
            json_data = response.json()
            opciones = [(str(item[id_key]), str(item[label_key])) for item in json_data if id_key in item and label_key in item]
            return [("", "Seleccione una opción...")] + opciones  # Agrega opción vacía al inicio
        return [("", "Seleccione una opción...")]

    def __init__(self, *args, **kwargs):
        super(ETSForm, self).__init__(*args, **kwargs)

        self.fields['idPeriodo'].choices = self.obtener_opciones("PeriodoToETS", "idPeriodo", "periodo")
        self.fields['idUA'].choices = self.obtener_opciones("UAprenToETS", "idUA", "nombre")

    idPeriodo = forms.ChoiceField(label="Periodo")
    idUA = forms.ChoiceField(label="Unidad de Aprendizaje")
    Turno = forms.ChoiceField(
        choices=[("", "Seleccione un turno..."), ("Matutino", "Matutino"), ("Vespertino", "Vespertino")],
        label="Turno"
    )
    Fecha = forms.DateField(widget=forms.DateInput(attrs={'type': 'date',
                                                        'min': date.today().strftime('%Y-%m-%d'),}))
    Cupo = forms.IntegerField(min_value=1)
    Duracion = forms.IntegerField(min_value=1)
    