/*
------------------------------------------------------------
cargarMedicion() --> void

PRE:
- El servidor REST está disponible.

PROCESO:
- Realizar GET a /api/medicion.
- Recibir la última medición en formato JSON.
- Separar fecha y hora.
- Mostrar los datos en la interfaz.

POST:
- La última medición aparece en pantalla.
- Si ocurre un error, se informa al usuario.
------------------------------------------------------------
*/
async function cargarMedicion() {

    const estado =
        document.getElementById("estado");

    estado.textContent =
        "Cargando...";

    try {

        const respuesta =
            await fetch("/api/medicion");


        if (!respuesta.ok) {

            throw new Error(
                "Error HTTP: "
                + respuesta.status
            );
        }


        const medicion =
            await respuesta.json();


        if (medicion === null) {

            document.getElementById("tipo").textContent = "-";
            document.getElementById("valor").textContent = "-";
            document.getElementById("fecha").textContent = "-";
            document.getElementById("hora").textContent = "-";

            estado.textContent =
                "No existen mediciones almacenadas.";

            return;
        }


        document.getElementById("tipo")
            .textContent =
            medicion.tipo;


        document.getElementById("valor")
            .textContent =
            medicion.valor;


        const fechaHora =
            medicion.fecha.split(" ");


        document.getElementById("fecha")
            .textContent =
            fechaHora[0] ?? "-";


        document.getElementById("hora")
            .textContent =
            fechaHora[1] ?? "-";


        estado.textContent =
            "Medición actualizada correctamente.";


    } catch (error) {

        console.error(error);

        estado.textContent =
            "No se pudo recuperar la medición.";
    }
}


/*
------------------------------------------------------------
inicializarInterfaz() --> void

PRE:
- El documento HTML ha terminado de cargar.

POST:
- Configura el botón Actualizar.
- Carga automáticamente la última medición.
------------------------------------------------------------
*/
function inicializarInterfaz() {

    const botonActualizar =
        document.getElementById("botonActualizar");


    botonActualizar.addEventListener(
        "click",
        cargarMedicion
    );


    cargarMedicion();
}


document.addEventListener(
    "DOMContentLoaded",
    inicializarInterfaz
);