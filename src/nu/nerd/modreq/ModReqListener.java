package nu.nerd.modreq;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nu.nerd.modreq.database.Request;
import nu.nerd.modreq.database.Request.RequestStatus;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

class ModReqListener implements Listener {
    private final ModReq plugin;

    public ModReqListener(ModReq instance) {
        plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("modreq.check")) {
            boolean includeElevated = event.getPlayer().hasPermission("modreq.cleardb");
            int open = plugin.reqTable.getTotalRequest(includeElevated, null, RequestStatus.OPEN, RequestStatus.CLAIMED);
            if (open > 0) {
//                event.getPlayer().sendMessage(ChatColor.GREEN + "There are " + open + " open mod requests. Type /check to see them.");
                event.getPlayer().sendMessage(Component.text("There are " + open + " open mod requests. Type /check to see them.", NamedTextColor.GREEN));
            }
        }

        List<Request> missedClosed = plugin.reqTable.getMissedClosedRequests(event.getPlayer().getUniqueId());

        for (Request req : missedClosed) {
        	String doneMessage = req.getCloseMessage();
    		String message = "";
    		if (doneMessage != null && doneMessage.length() != 0) {
    			message = String.format("%s completed your request - %s%s", req.getAssignedMod(), ChatColor.GRAY, doneMessage);
    		} else {
    			message = String.format("%s completed your request", req.getAssignedMod());
    		}
    		event.getPlayer().sendMessage(Component.text(message, NamedTextColor.GREEN));
        	req.setCloseSeenByUser(true);

        	plugin.reqTable.save(req);
        }
    }
}
