<template>
    <div>
        <HeaderComponent title="Perfil de usuario" />
        <v-container class="general-padding">
            <v-card>
                <v-card-title tabindex="0">Perfil del Usuario</v-card-title>
                <v-card-text v-if="authStore">
                    <v-card-text>
                        <v-form ref="form">
                            <v-row>
                                <v-col cols="6">
                                    <v-text-field
                                        label="Nombre"
                                        v-model="form.name"
                                        :rules="rules.name"
                                    ></v-text-field>
                                </v-col>
                                <v-col cols="6">
                                    <v-text-field
                                        label="Apellido"
                                        v-model="form.surname"
                                        :rules="rules.surname"
                                    ></v-text-field>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="6">
                                    <Datepicker
                                        v-model="form.birthdate"
                                        label="Fecha de nacimiento"
                                    ></Datepicker>
                                </v-col>
                                <v-col cols="6">
                                    <v-text-field
                                        label="Telefono"
                                        v-model="form.telephone"
                                        :rules="rules.telephone"
                                        type="number"
                                    ></v-text-field>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="6">
                                    <v-text-field
                                        label="Email"
                                        v-model="form.email"
                                        :rules="rules.email"
                                    ></v-text-field>
                                </v-col>
                                <v-col
                                    v-if="
                                        !(
                                            authStore.hasAuthority('TRAINER') ||
                                            authStore.hasAuthority('ADMIN')
                                        )
                                    "
                                    cols="6"
                                >
                                    <v-select
                                        :rules="rules.usertype"
                                        v-model="form.usertype"
                                        :items="allTypes"
                                        item-text="name"
                                        item-value="id"
                                        data-vv-name="select"
                                        label="Tipo de jugador"
                                        required
                                    ></v-select>
                                </v-col>
                                <v-col v-else cols="6">
                                    <v-text-field
                                        label="Rol asignado"
                                        v-model="role"
                                        readonly
                                    ></v-text-field>
                                </v-col>
                            </v-row>
                            <v-row>
                                <v-col cols="6">
                                    <v-text-field
                                        label="Nombre del usuario"
                                        v-model="form.username"
                                        :rules="rules.username"
                                    ></v-text-field>
                                </v-col>
                                <v-col cols="6">
                                    <v-text-field
                                        v-model="form.password"
                                        :append-icon="
                                            showPassword
                                                ? 'mdi-eye'
                                                : 'mdi-eye-off'
                                        "
                                        :rules="rules.password"
                                        :type="
                                            showPassword ? 'text' : 'password'
                                        "
                                        label="Contraseña del usuario"
                                        counter
                                        @click:append="
                                            showPassword = !showPassword
                                        "
                                    ></v-text-field>
                                </v-col>
                            </v-row>
                        </v-form>
                    </v-card-text>
                    <v-card-actions class="d-flex justify-end">
                        <v-btn
                            aria-label="Guardar"
                            color="rgba(34, 56, 67, 0.85)"
                            dark
                            @click="
                                if ($refs.form.validate()) {
                                    save();
                                }
                            "
                            >Guardar</v-btn
                        >
                    </v-card-actions>
                </v-card-text>
            </v-card>
        </v-container>
    </div>
</template>

<script>
import { localAxios } from "@/axios";
import HeaderComponent from "@/components/HeaderComponent.vue";
import { AuthStore } from "@/store/auth";
import { SnackbarStore } from "@/store/snackbar";
import Datepicker from "@/components/datepicker.vue";

export default {
    components: { Datepicker, HeaderComponent },
    data: () => ({
        user: null,
        authStore: null,
        allRoles: null,
        allTypes: null,
        showPassword: null,
        role: null,
        snackbarStore: null,
        form: {
            name: "",
            surname: "",
            telephone: "",
            email: "",
            username: "",
            usertype: null,
            password: "",
            birthdate: null,
            roles: [],
        },
        rules: {
            name: [(v) => !!v || "Se requiere un nombre"],
            surname: [(v) => !!v || "Se requiere un apellido"],
            telephone: [(v) => !!v || "Se requiere un telefono"],
            email: [
                (v) => !!v || "Se requiere un email",
                (v) =>
                    /^([\w-.]+@([\w-]+\.)+[\w-]{2,})?$/.test(v) ||
                    "El email debe tener un formato válido",
            ],
            birthdate: [(v) => !!v || "Se requiere una fecha de nacimiento"],
            username: [(v) => !!v || "Se requiere un nombre de usuario"],
            password: [(v) => !!v || "Se requiere una contraseña"],
        },
    }),
    async mounted() {
        this.snackbarStore = SnackbarStore();
        this.authStore = AuthStore();
        // ESTO TRAIA COMO 6 ELEMENTOS DE MAS EN ROLES
        // try {
        //     let response1 = await localAxios.get("/admin/users/get-user");
        //     this.user = response1.data;
        // } catch (error) {
        //     console.log(error);
        // }
        let response2 = await localAxios.get("/admin/users/get-types");
        this.allTypes = response2.data;
        this.form = this.authStore.user.user;

        this.role =
            this.authStore.user.user.roles[0].name === "TRAINER"
                ? "Entrenador"
                : "Administrador";
        let response = await localAxios.get("/admin/roles");
        this.allRoles = response.data;
    },
    methods: {
        async save() {
            this.form.telephone = parseInt(this.form.telephone);
            console.log(this.form);
            let response = await localAxios.put("/admin/users", this.form);
            this.snackbarStore.open("Guardado", "green");
        },
    },
};
</script>
