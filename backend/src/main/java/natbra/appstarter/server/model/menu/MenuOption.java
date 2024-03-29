package natbra.appstarter.server.model.menu;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MenuOption {

	//Home("Home", "/", "mdi-home", SubMenu.General.getName()),
	Profile("Perfil", "/profile", "mdi-account", SubMenu.General.getName()),
	Setting("Configuracion", "/setting", "mdi-cog", SubMenu.General.getName()),
	Users("Usuarios", "/users", "mdi-account-multiple", SubMenu.Admin.getName()),
	Security("Seguridad", "/security", "mdi-key", SubMenu.Admin.getName()),
	Players("Jugadores", "/players", "mdi-account-multiple", SubMenu.Trainer.getName()),
	//PlayersRequests("Pedidos de jugadores", "/players/requests", "mdi-account-alert", SubMenu.Trainer.getName()),
	Trainings("Entrenamientos", "/trainings", "mdi-run", SubMenu.Trainer.getName()),
	Planifications("Planificaciones", "/planifications", "mdi-file-account", SubMenu.Trainer.getName()),
	UsersPlanifications("Planificaciones de usuarios", "/users_planifications", "mdi-account-outline",
			SubMenu.Trainer.getName()),
	PlanificationsByPlayer("Entrenamientos asignados", "/users_planifications", "mdi-account-outline",
			SubMenu.Player.getName());
	private String name;
	private String url;
	private String icon;
	private String subMenu;

	private MenuOption(String name, String url, String icon, String subMenu) {
		this.name = name;
		this.url = url;
		this.icon = icon;
		this.subMenu = subMenu;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getIcon() {
		return icon;
	}

	public String getSubMenu() {
		return subMenu;
	}

}
