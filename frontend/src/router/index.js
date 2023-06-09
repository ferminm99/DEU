import { pinia } from "@/store";
import { AuthStore } from "@/store/auth";
import { ForbiddenError } from "@/store/errors/forbiddenError";
import AuthGeneralViewVue from "@/views/AuthGeneralView.vue";
import LoginViewVue from "@/views/LoginView.vue";
import RegisterViewVue from "@/views/RegisterView.vue";
import PlayersRequestsViewVue from "@/views/PlayersRequestsView";
import Vue from "vue";
import VueRouter from "vue-router";
import HomeView from "../views/HomeView.vue";
// import { authGuard } from "./guards/auth";

Vue.use(VueRouter);

const routes = [
    {
        path: "/login",
        name: "Login",
        component: LoginViewVue,
    },
    {
        path: "/register",
        name: "Register",
        component: RegisterViewVue,
    },
    {
        path: "/",
        component: AuthGeneralViewVue,
        children: [
            {
                path: "",
                name: "Home",
                component: HomeView,
            },

            {
                path: "/users",
                name: "Users",
                component: () => import("../views/UsersView"),
            },

            {
                path: "/security",
                name: "Security",
                component: () => import("@/views/SecurityView"),
            },
            {
                path: "/players/requests",
                name: "PlayersRequests",
                component: PlayersRequestsViewVue,
            },
            {
                path: "/players",
                component: () => import("@/views/PlayersView"),
                children: [
                    {
                        path: "",
                        name: "Players",
                        component: () => import("@/views/PlayersView"),
                    },
                    {
                        path: "edit",
                        name: "Edit_Player",
                        component: () =>
                            import("@/views/PlayersView/editPlayer"),
                        props: true,
                    },
                ],
            },
            {
                path: "/trainings",
                component: () => import("@/views/TrainingsView"),
                children: [
                    {
                        path: "",
                        name: "Trainings",
                        component: () => import("@/views/TrainingsView"),
                    },
                    {
                        path: "edit",
                        name: "Edit_Training",
                        component: () =>
                            import("@/views/TrainingsView/editTraining"),
                        props: true,
                    },
                ],
            },
            {
                path: "/planifications",
                component: () => import("@/views/PlanificationsView"),
                children: [
                    {
                        path: "",
                        name: "Planifications",
                        component: () => import("@/views/PlanificationsView"),
                    },
                    {
                        path: "edit",
                        name: "Edit_Planification",
                        component: () =>
                            import(
                                "@/views/PlanificationsView/editPlanification"
                            ),
                        props: true,
                    },
                ],
            },
            // {
            //     path: "/users_planifications",
            //     component: () => import("@/views/UsersPlanificationsView"),
            //     children: [
            //         {
            //             path: "",
            //             name: "Users_Planifications",
            //             component: () =>
            //                 import("@/views/UsersPlanificationsView"),
            //         },
            //     ],
            // },
            {
                path: "/users_planifications",
                name: "UsersPlanifications",
                component: () => import("../views/UsersPlanificationsView"),
            },
            {
                path: "/profile",
                name: "Profile",
                component: () => import("@/views/ProfileView"),
            },
            {
                path: "/setting",
                name: "Setting",
                component: () => import("@/views/SettingView"),
            },
        ],
    },
];

const router = new VueRouter({
    // mode: "history",
    base: process.env.BASE_URL,
    routes,
});

router.beforeEach((to, from, next) => {
    const authStore = AuthStore(pinia);
    if (!authStore.isLogged() && to.name != "Login" && to.name != "Register") {
        next({ name: "Login" });
    } else if (
        to.name != "Login" &&
        to.name != "Register" &&
        !authStore.hasAuthority("CAN_VIEW_" + to.name)
    ) {
        let forbiddenError = ForbiddenError();
        forbiddenError.open();
        return false;
    } else {
        next();
    }
});

export default router;
