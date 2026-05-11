-- 1. Tabla de Perfiles (Extensión de Auth.users)
CREATE TABLE profiles (
    id UUID REFERENCES auth.users ON DELETE CASCADE PRIMARY KEY,
    name TEXT,
    last_name TEXT,
    email TEXT UNIQUE,
    role TEXT DEFAULT 'PATIENT', -- 'PATIENT' o 'CAREGIVER'
    phone_number TEXT,
    birth_date DATE,
    condition TEXT,
    wellness_score INTEGER DEFAULT 0,
    avatar_url TEXT,
    fcm_token TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 2. Tabla de Entradas de Diario
CREATE TABLE diary_entries (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users ON DELETE CASCADE NOT NULL,
    title TEXT,
    content TEXT,
    mood TEXT, -- 'Happy', 'Anxious', 'Sad', etc.
    tags TEXT[],
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 3. Tabla de Recordatorios
CREATE TABLE reminders (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users ON DELETE CASCADE NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    time TEXT NOT NULL, -- "HH:mm"
    days TEXT[], -- ["Mon", "Tue", etc.]
    is_enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 4. Tabla de Vínculos Paciente-Cuidador
CREATE TABLE patient_caregiver_links (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    patient_id UUID REFERENCES auth.users ON DELETE CASCADE NOT NULL,
    caregiver_id UUID REFERENCES auth.users ON DELETE CASCADE NOT NULL,
    status TEXT DEFAULT 'pending', -- 'pending', 'active', 'rejected'
    relationship TEXT, -- 'Familiar', 'Terapeuta', etc.
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    UNIQUE(patient_id, caregiver_id)
);

-- 5. Tabla de Dispositivos y Tokens FCM
CREATE TABLE user_devices (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users ON DELETE CASCADE NOT NULL,
    fcm_token TEXT NOT NULL,
    device_name TEXT,
    last_seen TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    UNIQUE(user_id, fcm_token)
);

-- 6. Tabla de Códigos Temporales de Vinculación
CREATE TABLE temp_link_codes (
    code TEXT PRIMARY KEY,
    user_id UUID REFERENCES auth.users ON DELETE CASCADE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- POLÍTICAS DE RLS (Row Level Security)

ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE diary_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE reminders ENABLE ROW LEVEL SECURITY;
ALTER TABLE patient_caregiver_links ENABLE ROW LEVEL SECURITY;

-- Perfiles: Cada uno ve el suyo, y los cuidadores ven los de sus pacientes activos.
CREATE POLICY "Users can view own profile" ON profiles FOR SELECT USING (auth.uid() = id);
CREATE POLICY "Caregivers can view linked patient profiles" ON profiles FOR SELECT 
USING (EXISTS (
    SELECT 1 FROM patient_caregiver_links 
    WHERE caregiver_id = auth.uid() AND patient_id = profiles.id AND status = 'active'
));

-- Diario: El dueño ve todo, y el cuidador ve el diario de sus pacientes activos.
CREATE POLICY "Users can manage own diary" ON diary_entries FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "Caregivers can view patient diary" ON diary_entries FOR SELECT
USING (EXISTS (
    SELECT 1 FROM patient_caregiver_links 
    WHERE caregiver_id = auth.uid() AND patient_id = diary_entries.user_id AND status = 'active'
));

-- Vínculos: Ambos involucrados pueden ver el vínculo.
CREATE POLICY "Users can view own links" ON patient_caregiver_links FOR SELECT 
USING (auth.uid() = patient_id OR auth.uid() = caregiver_id);

-- Dispositivos: Cada uno gestiona los suyos.
ALTER TABLE user_devices ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can manage own devices" ON user_devices FOR ALL USING (auth.uid() = user_id);
