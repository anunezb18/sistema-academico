-- Script para agregar la columna periodo a la tabla Boletin
-- Ejecutar este script en la base de datos PostgreSQL

-- Agregar columna periodo a la tabla Boletin
ALTER TABLE "Boletin"
ADD COLUMN IF NOT EXISTS "periodo" INTEGER;

-- Agregar comentario a la columna
COMMENT ON COLUMN "Boletin"."periodo" IS 'Periodo académico: 1=Primer Periodo, 2=Segundo Periodo, 3=Tercer Periodo, 4=Cuarto Periodo';

-- Actualizar registros existentes con valor por defecto (si existen)
UPDATE "Boletin"
SET "periodo" = 1
WHERE "periodo" IS NULL;

