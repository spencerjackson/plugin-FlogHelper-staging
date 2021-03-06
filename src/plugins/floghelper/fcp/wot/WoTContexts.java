/* FlogHelper, Freenet plugin to create flogs
 * Copyright (C) 2009 Romain "Artefact2" Dalmaso
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package plugins.floghelper.fcp.wot;

import freenet.pluginmanager.PluginNotFoundException;
import freenet.support.SimpleFieldSet;
import freenet.support.api.Bucket;
import plugins.floghelper.fcp.ReceptorCore;
import plugins.floghelper.fcp.SyncPluginTalker;

/**
 * Used to manage WoT contexts and properties.
 *
 * @author Artefact2
 */
public class WoTContexts {
	public static final String FLOGHELPER_CONTEXT = "Flog";

	public static void addContext(String authorID) throws PluginNotFoundException {
		final SimpleFieldSet sfs = new SimpleFieldSet(true);
		sfs.putOverwrite("Message", "AddContext");
		sfs.putOverwrite("Identity", authorID);
		sfs.putOverwrite("Context", WoTContexts.FLOGHELPER_CONTEXT);

		SyncPluginTalker spt = new SyncPluginTalker(new ReceptorCore() {

			public void onReply(String pluginname, String indentifier, SimpleFieldSet params, Bucket data) {
			}
		}, sfs, null);

		spt.run();
	}

	public static void addProperty(String authorID, String propertyName, String propertyValue) throws PluginNotFoundException {
		final SimpleFieldSet sfs = new SimpleFieldSet(true);
		sfs.putOverwrite("Message", "SetProperty");
		sfs.putOverwrite("Identity", authorID);
		sfs.putOverwrite("Property", propertyName);
		sfs.putOverwrite("Value", propertyValue);

		SyncPluginTalker spt = new SyncPluginTalker(new ReceptorCore() {

			public void onReply(String pluginname, String indentifier, SimpleFieldSet params, Bucket data) {
			}
		}, sfs, null);

		spt.run();
	}
}
