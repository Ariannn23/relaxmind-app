import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from 'https://esm.sh/@supabase/supabase-js@2'

serve(async (req) => {
  const { recipientUserId, title, body, data } = await req.json()

  // 1. Inicializar cliente Supabase con Service Role Key (para saltar RLS)
  const supabase = createClient(
    Deno.env.get('SUPABASE_URL') ?? '',
    Deno.env.get('SUPABASE_SERVICE_ROLE_KEY') ?? ''
  )

  // 2. Obtener tokens FCM del usuario destinatario
  const { data: devices, error } = await supabase
    .from('user_devices')
    .select('fcm_token')
    .eq('user_id', recipientUserId)

  if (error || !devices || devices.length === 0) {
    return new Response(JSON.stringify({ error: 'No devices found' }), { status: 404 })
  }

  // 3. Enviar notificaciones vía FCM API v1
  // NOTA: Requiere configuración de FIREBASE_SERVICE_ACCOUNT en Supabase Secrets
  const results = await Promise.all(devices.map(async (device) => {
    return await sendFcmNotification(device.fcm_token, title, body, data)
  }))

  return new Response(JSON.stringify({ results }), { status: 200 })
})

async function sendFcmNotification(token: string, title: string, body: string, data: any) {
  // Aquí se implementaría la llamada a la API de Google FCM v1.
  // Es necesario obtener un Access Token de Google usando la Service Account.
  // Por brevedad, se muestra la estructura del fetch:
  
  /*
  const response = await fetch(`https://fcm.googleapis.com/v1/projects/${PROJECT_ID}/messages:send`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${accessToken}`,
    },
    body: JSON.stringify({
      message: {
        token: token,
        notification: { title, body },
        data: data
      }
    })
  })
  return response.json()
  */
  
  // Simulacro de éxito para la estructura del código
  return { success: true, token }
}
