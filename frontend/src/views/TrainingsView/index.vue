<template>
    <div>
        <HeaderComponent title="Listado de mis entrenamientos" />
        <v-container class="general-padding">
            <v-card>
                <v-card-title tabindex="0"> Entrenamientos </v-card-title>
                <v-card-text>
                    <v-text-field
                        v-model="datatable.search"
                        label="Buscador"
                        hide-details
                    >
                        <template v-slot:append-outer>
                            <v-btn
                                color="rgba(34, 56, 67, 0.85)"
                                dark
                                @click="dialogs.addTraining = true"
                            >
                                Agregar entrenamiento
                            </v-btn>
                        </template>
                    </v-text-field>
                    <v-data-table
                        :headers="datatable.headers"
                        :items="datatable.items"
                        :search="datatable.search"
                        :items-per-page="10"
                        :sort-by="['date']"
                        :sort-asc="true"
                        class="elevation-0"
                        tabindex="0"
                    >
                        <template v-slot:item="{ item }">
                            <tr>
                                <td tabindex="0">{{ item.date }}</td>
                                <td tabindex="0">{{ item.name }}</td>
                                <td tabindex="0">{{ item.description }}</td>
                                <td tabindex="0">
                                    {{ item.training_type.name }}
                                </td>
                                <td tabindex="0">{{ item.warmup_time }}</td>
                                <td tabindex="0">
                                    {{ item.repetitions_quantity }}
                                </td>
                                <td>
                                    <v-tooltip top>
                                        <template
                                            v-slot:activator="{ on, attrs }"
                                        >
                                            <v-btn
                                                v-bind="attrs"
                                                v-on="on"
                                                icon
                                                :style="{
                                                    marginTop: '0px',
                                                }"
                                                aria-label="Editar entrenamiento"
                                                @click="editTraining(item)"
                                            >
                                                <v-icon>mdi-pencil</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Editar entrenamiento</span>
                                    </v-tooltip>
                                </td>
                                <td>
                                    <v-tooltip top>
                                        <template
                                            v-slot:activator="{ on, attrs }"
                                        >
                                            <v-btn
                                                v-bind="attrs"
                                                v-on="on"
                                                icon
                                                :style="{
                                                    marginTop: '0px',
                                                }"
                                                aria-label="Borrar entrenamiento"
                                                @click="deleteTraining(item)"
                                            >
                                                <v-icon>mdi-delete</v-icon>
                                            </v-btn>
                                        </template>
                                        <span>Borrar entrenamiento</span>
                                    </v-tooltip>
                                </td>
                            </tr>
                        </template>
                    </v-data-table>
                </v-card-text>
            </v-card>
        </v-container>
        <AddTraining v-model="dialogs.addTraining" @saved="newTrainingSaved" />
        <EditTraining
            v-model="dialogs.editTraining.show"
            :training="dialogs.editTraining.training"
            @saved="savededitTraining"
        />
        <v-dialog v-model="dialogs.deleteTraining" max-width="500px">
            <v-card>
                <v-card-title>Confirmar eliminación</v-card-title>
                <v-card-text>
                    ¿Deseas eliminar el entrenamiento seleccionado?
                </v-card-text>
                <v-card-actions>
                    <v-btn
                        color="rgba(34, 56, 67, 0.85)"
                        dark
                        @click="dialogs.deleteTraining = false"
                    >
                        <v-icon left>mdi-close</v-icon>
                        Cancelar</v-btn
                    >
                    <v-btn
                        color="rgba(34, 56, 67, 0.85)"
                        dark
                        @click="confirmDelete"
                    >
                        <v-icon left>mdi-delete</v-icon>
                        Eliminar</v-btn
                    >
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
import { localAxios } from "@/axios";
import HeaderComponent from "@/components/HeaderComponent.vue";
import AddTraining from "./addTraining.vue";
import EditTraining from "./editTraining.vue";

export default {
    components: {
        HeaderComponent,
        AddTraining,
        EditTraining,
    },
    data: () => ({
        dialogs: {
            addTraining: false,
            editTraining: { show: false, training: null },
            deleteTraining: false,
        },
        esAdmin: false,
        trainingToDelete: null,
        selectedUser: null,
        users: [],
        datatable: {
            items: [],
            headers: [
                {
                    text: "Fecha",
                    align: "start",
                    sortable: true,
                    value: "date",
                    sortIcon: "mdi-arrow-up-down",
                },
                {
                    text: "Nombre",
                    value: "name",
                    sortable: null,
                },
                {
                    text: "Descripcion",
                    value: "description",
                    sortable: null,
                },
                {
                    text: "Tipo de entrenamiento",
                    value: "training_type.name",
                    sortable: null,
                },
                {
                    text: "Tiempo de calentamiento",
                    value: "warmup_time",
                    sortable: null,
                },
                {
                    text: "Cantidad de repeticiones",
                    value: "repetitions_quantity",
                    sortable: null,
                },
                { text: "Editar", value: "", sortable: null },
                { text: "Borrar", value: "", sortable: null },
            ],
            trainings: [],
            search: "",
        },
    }),
    async mounted() {
        let response = await localAxios.get("/admin/trainings");
        this.datatable.items = response.data;
    },
    methods: {
        newTrainingSaved(newTraining) {
            this.datatable.items.push(newTraining);
        },
        savededitTraining(newTraining) {
            const index = this.datatable.items.findIndex(
                (i) => i.id === newTraining.id
            );
            this.datatable.items.splice(index, 1, newTraining);
        },
        requestBloqueado() {
            localAxios.get("/api/blocked").then(() => {});
        },
        deleteTraining(item) {
            this.dialogs.deleteTraining = true;
            this.trainingToDelete = item;
        },
        async confirmDelete() {
            this.dialogs.deleteTraining = false;
            let response = await localAxios.delete("/admin/trainings", {
                data: this.trainingToDelete,
            });
            if (response.status == 200) {
                const index = this.datatable.items.indexOf(
                    this.trainingToDelete
                );
                if (index >= 0) this.datatable.items.splice(index, 1);
            }
            this.trainingToDelete = null;
        },
        async editTraining(item) {
            // this.$router.push({
            //     name: "Edit_Training",
            //     params: {
            //         training: item,
            //     },
            // });
            this.dialogs.editTraining.training = item;
            this.dialogs.editTraining.show = true;
        },
    },
};
</script>
