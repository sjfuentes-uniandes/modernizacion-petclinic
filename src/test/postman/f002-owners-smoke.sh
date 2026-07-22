#!/usr/bin/env bash
#
# F002 - Gestión de propietarios mediante API REST.
#
# Ejercita la API REST de propietarios recolectando las evidencias que pide la sección de instrumentación
# del pre-experimento: cantidad de solicitudes GET/POST/PUT correctas, porcentaje de respuestas exitosas,
# validación de los campos, rechazo de solicitudes inválidas y asociación con una mascota existente.
#
# Uso:
#   ./src/test/postman/f002-owners-smoke.sh [baseUrl]
#
# Requisitos: la aplicación en ejecución (java -jar target/petclinic.jar) y las utilidades curl y sed.

set -u

BASE_URL="${1:-http://localhost:8000}"
API="${BASE_URL}/api/owners"

TOTAL=0
OK=0
GET_OK=0
POST_OK=0
PUT_OK=0
FAILED_CASES=()

# check <descripción> <método> <código esperado> <código obtenido> <cuerpo>
check() {
    local description="$1" method="$2" expected="$3" actual="$4" body="$5"
    TOTAL=$((TOTAL + 1))
    if [ "$actual" = "$expected" ]; then
        OK=$((OK + 1))
        case "$method" in
            GET) GET_OK=$((GET_OK + 1)) ;;
            POST) POST_OK=$((POST_OK + 1)) ;;
            PUT) PUT_OK=$((PUT_OK + 1)) ;;
        esac
        printf '  OK    [%s %s] %s\n' "$method" "$actual" "$description"
    else
        FAILED_CASES+=("$description (esperado $expected, obtenido $actual)")
        printf '  FALLO [%s %s] %s -- se esperaba %s\n' "$method" "$actual" "$description" "$expected"
    fi
    printf '        %s\n' "$body"
}

# request <método> <url> [cuerpo json] -> imprime "<código>\n<cuerpo>"
request() {
    local method="$1" url="$2" data="${3:-}"
    if [ -n "$data" ]; then
        curl -s -w '\n%{http_code}' -X "$method" "$url" -H 'Content-Type: application/json' -d "$data"
    else
        curl -s -w '\n%{http_code}' -X "$method" "$url"
    fi
}

status_of() { printf '%s' "$1" | tail -n 1; }
body_of() { printf '%s' "$1" | sed '$d'; }

echo "F002 - API REST de propietarios en ${API}"
echo

echo "== Casos válidos =="

RESPONSE=$(request GET "${API}")
check "Listar todos los propietarios" GET 200 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE" | cut -c1-160)..."

RESPONSE=$(request GET "${API}?lastName=Davis")
check "Buscar propietarios por apellido" GET 200 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE" | cut -c1-160)..."

RESPONSE=$(request GET "${API}/1")
check "Consultar el propietario 1" GET 200 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

NEW_OWNER='{"firstName":"Ana","lastName":"Zuluaga","address":"Calle 1 #2-3","city":"Bogota","telephone":"3001234567"}'
RESPONSE=$(request POST "${API}" "$NEW_OWNER")
CREATED_BODY=$(body_of "$RESPONSE")
check "Crear un propietario" POST 201 "$(status_of "$RESPONSE")" "$CREATED_BODY"
OWNER_ID=$(printf '%s' "$CREATED_BODY" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')

if [ -z "$OWNER_ID" ]; then
    echo
    echo "No se pudo obtener el id del propietario creado; se omiten los casos que dependen de él."
else
    UPDATED_OWNER='{"firstName":"Ana Maria","lastName":"Zuluaga","address":"Carrera 9 #10-11","city":"Medellin","telephone":"3009876543"}'
    RESPONSE=$(request PUT "${API}/${OWNER_ID}" "$UPDATED_OWNER")
    check "Actualizar el propietario ${OWNER_ID}" PUT 200 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

    RESPONSE=$(request POST "${API}/${OWNER_ID}/pets/1")
    check "Vincular la mascota 1 con el propietario ${OWNER_ID}" POST 200 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

    RESPONSE=$(request GET "${API}/${OWNER_ID}")
    check "Confirmar los cambios del propietario ${OWNER_ID}" GET 200 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"
fi

echo
echo "== Casos inválidos =="

RESPONSE=$(request POST "${API}" '{"firstName":"Ana","lastName":"Zuluaga","telephone":"3001234567"}')
check "Rechazar la creación sin dirección ni ciudad" POST 400 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

RESPONSE=$(request POST "${API}" '{"firstName":"","lastName":"Zuluaga","address":"Calle 1","city":"Bogota","telephone":"tres-mil"}')
check "Rechazar el nombre vacío y el teléfono no numérico" POST 400 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

RESPONSE=$(request POST "${API}" '{"firstName":')
check "Rechazar un JSON malformado" POST 400 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

RESPONSE=$(request GET "${API}/9999")
check "Responder 404 al consultar un propietario inexistente" GET 404 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

RESPONSE=$(request PUT "${API}/9999" '{"firstName":"Ana","lastName":"Zuluaga","address":"Calle 1","city":"Bogota","telephone":"3001234567"}')
check "Responder 404 al actualizar un propietario inexistente" PUT 404 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

RESPONSE=$(request POST "${API}/${OWNER_ID:-1}/pets/9999")
check "Responder 404 al vincular una mascota inexistente" POST 404 "$(status_of "$RESPONSE")" "$(body_of "$RESPONSE")"

echo
echo "== Resumen =="
printf 'Solicitudes ejecutadas correctamente: GET=%s POST=%s PUT=%s\n' "$GET_OK" "$POST_OK" "$PUT_OK"
printf 'Casos con el resultado esperado: %s de %s (%s%%)\n' "$OK" "$TOTAL" "$((OK * 100 / TOTAL))"

if [ "${#FAILED_CASES[@]}" -gt 0 ]; then
    echo
    echo "Casos fallidos:"
    for case_name in "${FAILED_CASES[@]}"; do
        printf '  - %s\n' "$case_name"
    done
    exit 1
fi

echo
echo "Todos los casos de F002 se comportaron como se esperaba."
