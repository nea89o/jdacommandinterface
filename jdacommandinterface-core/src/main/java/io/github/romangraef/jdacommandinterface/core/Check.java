package io.github.romangraef.jdacommandinterface.core;

import net.dv8tion.jda.api.Permission;

public enum Check {
    DEVELOPER_ONLY() {
        @Override
        public boolean check(Context context) {
            return context.getCommandListener().getAdmins().contains(context.getAuthor().getId());
        }

        @Override
        public String getDescription() {
            return "Just for developer";
        }
    },
    ADMIN_ONLY() {
        @Override
        public boolean check(Context context) {
            return DEVELOPER_ONLY.check(context) ||
                    (context.getMember() != null && context.getMember().hasPermission(Permission.MANAGE_SERVER));
        }

        @Override
        public String getDescription() {
            return "For this command you need `MANAGE_SERVER` permissions on this discord to execute this command";
        }
    };

    abstract boolean check(Context context);

    /**
     * Error description
     *
     * @return a generic description
     */
    abstract String getDescription();

}
