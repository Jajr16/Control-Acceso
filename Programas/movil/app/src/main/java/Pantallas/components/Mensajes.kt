package Pantallas.components

import androidx.compose.runtime.Composable
//
//@Composable
//fun MessageBubble(mensaje: Mensaje, usuarioActual: String) {
//    val isSentByMe = mensaje.remitente == usuarioActual
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = if (isSentByMe) Arrangement.End else Arrangement.Start
//    ) {
//        Box(
//            modifier = Modifier
//                .background(if (isSentByMe) Color.Blue else Color.Gray, shape = RoundedCornerShape(8.dp))
//                .padding(8.dp)
//        ) {
//            Text(text = mensaje.mensaje, color = Color.White)
//        }
//    }
//}