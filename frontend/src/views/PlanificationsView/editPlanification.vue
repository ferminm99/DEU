<template>
    <v-overlay :value="localShow">
        <v-dialog v-model="localShow" hide-overlay persistent>
            <v-card style="width: 600px">
                <v-card-title
                    class="d-flex justify-space-between align-center mb-4"
                >
                    <div>Editar Planificacion</div>
                    <div>
                        <v-btn icon @click="closeAll()" aria-label="Cerrar">
                            <v-icon>mdi-close</v-icon>
                        </v-btn>
                    </div>
                </v-card-title>
                <v-card-text style="max-height: 500px; overflow-y: auto">
                    <v-row>
                        <v-col cols="12">
                            <v-text-field
                                v-model="name"
                                label="Nombre de la planificación"
                                required
                            ></v-text-field>
                        </v-col>
                    </v-row>

                    <v-row>
                        <v-col cols="12">
                            <b>
                                <label for="training-select">
                                    Seleccione entrenamiento
                                </label>
                            </b>
                            <multiselect
                                v-model="valueMultiselect"
                                id="training-select"
                                placeholder="Seleccione entrenamiento"
                                label="name"
                                :options="trainings"
                                :multiple="true"
                                :close-on-select="false"
                                @input="onSelect"
                            >
                            </multiselect>
                        </v-col>
                    </v-row>

                    <v-row>
                        <v-col cols="12">
                            <li>
                                <b><span>Lista de entrenamientos</span></b>
                            </li>
                            <li>
                                <div class="item-container">
                                    <v-col style="margin-left: 5px">
                                        <b>
                                            <label for="minutes-input"
                                                >Minutos</label
                                            ></b
                                        >
                                    </v-col>
                                    <v-col style="margin-left: 112px">
                                        <b>
                                            <span for="name-input"
                                                >Nombre</span
                                            ></b
                                        >
                                    </v-col>
                                    <v-col style="margin-left: 196px">
                                        <b><span>Borrar</span></b>
                                    </v-col>
                                </div>
                            </li>
                            <draggable
                                v-model="selectedTrainings"
                                :element="'ul'"
                                class="sortable-list"
                                @end="actualizarOrden"
                            >
                                <li
                                    v-for="(
                                        item, index
                                    ) in selectedTrainingsSorted"
                                    :key="item.id"
                                    :class="{ deleting: item.deleting }"
                                >
                                    <div
                                        class="item-container"
                                        style="display: flex"
                                    >
                                        <input
                                            type="number"
                                            v-model="item.minutes"
                                            class="item-minutes"
                                            min="0"
                                            id="minutes-input"
                                            placeholder="Seleccione minutos..."
                                        />
                                        <div class="item-training-name">
                                            <span>
                                                {{ item.training.name }}</span
                                            >
                                        </div>
                                    </div>
                                    <v-tooltip top>
                                        <template
                                            v-slot:activator="{ on, attrs }"
                                        >
                                            <v-btn
                                                class="delete-button"
                                                v-bind="attrs"
                                                v-on="on"
                                                icon
                                                :style="{
                                                    marginTop: '0px',
                                                }"
                                                aria-label="Quitar entrenamiento"
                                                @click="confirmDelete(index)"
                                            >
                                                <v-icon>mdi-delete</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Quitar entrenamiento</span>
                                    </v-tooltip>
                                </li>
                            </draggable>
                        </v-col>
                    </v-row>
                </v-card-text>
                <v-card-actions class="d-flex justify-end">
                    <v-btn
                        type="submit"
                        @click="save()"
                        aria-label="Agregar"
                        color="rgba(34, 56, 67, 0.85)"
                        dark
                        >Confirmar</v-btn
                    >
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-dialog v-model="confirmDialog" max-width="500px">
            <v-card>
                <v-card-title tabindex="0">Confirmar eliminación</v-card-title>
                <v-card-text>
                    ¿Estás seguro de que deseas quitar el entrenamiento?
                </v-card-text>
                <v-card-actions>
                    <v-btn
                        color="rgba(34, 56, 67, 0.85)"
                        dark
                        @click="cancelDelete"
                    >
                        <v-icon left>mdi-close</v-icon>
                        Cancelar</v-btn
                    >
                    <v-btn
                        color="rgba(34, 56, 67, 0.85)"
                        dark
                        @click="deleteItem"
                    >
                        <v-icon left>mdi-delete</v-icon>
                        Eliminar</v-btn
                    >
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-overlay>
</template>

<script>
import { localAxios } from "@/axios";
import Multiselect from "vue-multiselect";
import moment from "moment";
import draggable from "vuedraggable";
export default {
    components: {
        Multiselect,
        draggable,
    },
    props: {
        value: { type: Boolean },
        planification: { type: Object },
    },
    data: () => ({
        localShow: false,
        selectedPlanification: null,
        //showPassword: false,
        trainings: [],
        players: [],
        name: "",
        valueMultiselect: [],
        selectedTrainings: [],
        trainingsBefore: [],
        confirmDialog: false,
    }),
    created() {
        this.selectedPlanification = this.planification;
        // realizar otras acciones necesarias en el ciclo de vida created
    },
    watch: {
        value: function (val) {
            this.localShow = val;
        },
        planification: function (val) {
            this.selectedPlanification = val;
        },

        selectedPlanification: {
            handler: async function (val) {
                // Hacer una copia superficial del objeto observado
                const selectedPlanificationCopy = Object.assign({}, val);

                console.log(selectedPlanificationCopy);
                let response = await localAxios
                    .post(
                        "/admin/planifications/retrieve",
                        selectedPlanificationCopy
                    )
                    .then((response) => {
                        this.selectedTrainings = response.data;
                        this.name = this.selectedPlanification.name;
                        console.log("TRAININGS: ");
                        console.log(this.selectedTrainings);
                    })
                    .catch((error) => {
                        // Manejar el error
                        console.error(error);
                    });
            },
            deep: true, // Observa los cambios profundos en el objeto
        },
    },

    computed: {
        selectedTrainingsSorted() {
            return this.selectedTrainings
                .slice()
                .sort((a, b) => a.orderNumber - b.orderNumber);
        },
    },

    async mounted() {
        let response = await localAxios.get("/admin/trainings");
        this.trainings = response.data;
        let response2 = await localAxios.get("/admin/users/get-players");
        this.players = response2.data;
    },
    methods: {
        onSelect() {
            const nuevoObjeto = {
                planification: null,
                training: this.valueMultiselect[0],
                minutes: null,
                orderNumber: this.selectedTrainings.length,
            };
            this.selectedTrainings.push(nuevoObjeto);
            this.valueMultiselect = [];
            console.log(this.selectedTrainings);
        },

        getElementsNotInArray(array1, array2) {
            const elementsNotInArray = [];

            array1.forEach((element) => {
                const id = element.id;
                if (!array2.some((el) => el.id === id)) {
                    elementsNotInArray.push(element);
                }
            });

            return elementsNotInArray;
        },

        actualizarOrden() {
            this.selectedTrainings.forEach((item, index) => {
                item.orderNumber = index;
            });
            console.log(this.selectedTrainings);
        },

        borrarItem(index) {
            this.selectedTrainings.splice(index, 1);
        },

        closeAll() {
            this.valueMultiselect = [];

            //al abrir de vuelta la misma ventana 2 veces si borro el selected trainings no aparecen los que tenias antes, asi que hay que recuperarlos...
            localAxios
                .post(
                    "/admin/planifications/retrieve",
                    this.selectedPlanification
                )
                .then((response) => {
                    this.selectedTrainings = response.data;
                })
                .catch((error) => {
                    // Manejar el error
                    console.error(error);
                });

            this.$emit("input", false);
        },

        confirmDelete(index) {
            this.deleteIndex = index;
            this.confirmDialog = true;
        },

        cancelDelete() {
            this.confirmDialog = false;
        },

        deleteItem() {
            this.confirmDialog = false;
            this.borrarItem(this.deleteIndex);
        },

        async save() {
            if (this.name != "") {
                if (this.selectedTrainings.length > 0) {
                    for (const element of this.selectedTrainings) {
                        if (
                            element.minutes != null &&
                            this.selectedTrainings.indexOf(element) ===
                                this.selectedTrainings.length - 1
                        ) {
                            for (
                                let i = 0;
                                i < this.selectedTrainings.length;
                                i++
                            ) {
                                this.selectedTrainings[i].minutes = parseInt(
                                    this.selectedTrainings[i].minutes
                                );
                            }

                            await localAxios
                                .post(
                                    "/admin/planifications/retrieve",
                                    this.selectedPlanification
                                )
                                .then((response) => {
                                    this.trainingsBefore = response.data;
                                    //Aca enviamos la planificacion de los entrenamientos con sus duraciones, orden y minutos
                                    localAxios
                                        .put(
                                            "/admin/planifications/trainers",
                                            {
                                                trainerPlanificationList:
                                                    this.selectedTrainings,
                                                name: this.name,
                                                id: this.selectedPlanification
                                                    .id,
                                            },
                                            {
                                                headers: {
                                                    "Content-Type":
                                                        "application/json",
                                                },
                                            }
                                        )
                                        .then((responsePlanification) => {
                                            //Aca borro las cosas que saque de la lista de entrenamientos, es decir si teniamos el
                                            // entrenamientos 1 2 3 y deje solo 1 2, deberian cargarse esos y eliminarse el 3
                                            // aca se elimina el 3, arriba se hizo el put para cargar los cambios de esos 2, por ej en sus mins o el name de la planification

                                            let elementsToDelete = [];
                                            elementsToDelete =
                                                this.getElementsNotInArray(
                                                    this.trainingsBefore,
                                                    this.selectedTrainings
                                                );

                                            console.log(elementsToDelete);

                                            if (elementsToDelete.length > 0) {
                                                localAxios
                                                    .delete(
                                                        "/admin/planifications/trainers",
                                                        {
                                                            data: elementsToDelete,
                                                        }
                                                    )
                                                    .then((response2) => {
                                                        this.$emit(
                                                            "saved",
                                                            responsePlanification.data
                                                        );
                                                        this.closeAll();
                                                    })
                                                    .catch((error) => {
                                                        // Manejar el error
                                                        console.error(error);
                                                    });
                                            } else {
                                                this.$emit(
                                                    "saved",
                                                    responsePlanification.data
                                                );
                                                this.closeAll();
                                            }
                                        })
                                        .catch((error) => {
                                            // manejar errores
                                        });
                                });
                        } else {
                            if (
                                this.selectedTrainings.indexOf(element) ===
                                this.selectedTrainings.length - 1
                            ) {
                                alert(
                                    "Debes ingresar minutos en cada entrenamiento"
                                );
                            }
                        }
                    }
                } else {
                    alert(
                        "Se requieren los jugadores y/o entrenamientos que asignara la planificacion"
                    );
                }
            } else {
                alert("Escribe un nombre y fecha");
            }
        },
    },
};
</script>
<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>
<style>
.sortable-list {
    list-style-type: none;
    padding: 0;
}
li {
    display: flex;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #ccc;
    transition: background-color 0.3s ease;
}

li.deleting {
    background-color: #ffeaea;
}

.delete-button {
    margin-left: auto;
    color: #ff5252;
    border: none;
    background: none;
    cursor: pointer;
}

.delete-button:hover {
    color: #d32f2f;
}

.item-container {
    display: flex;
    justify-content: space-between;
}

.item-minutes {
    width: 155px;
    box-sizing: border-box;
    padding: 5px;
}

.item-training-name {
    width: 100px;
    margin-left: 25px;
}
</style>
