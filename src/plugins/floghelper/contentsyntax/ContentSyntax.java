/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package plugins.floghelper.contentsyntax;

import freenet.support.HTMLNode;
import freenet.support.Logger;
import java.util.Vector;
import plugins.floghelper.contentsyntax.js.JavascriptFactoryToadlet;
import plugins.floghelper.ui.FlogHelperToadlet;

/**
 *
 * @author Artefact2
 */
public abstract class ContentSyntax {

	public enum Syntaxes {

		YAWKL, RawXHTML
	}
	protected final Vector<SyntaxElement> syntaxElements = new Vector<SyntaxElement>();

	public String parseSomeString(String s) {
		for (SyntaxElement e : this.syntaxElements) {
			s = e.regex.matcher(s).replaceAll(e.xHTMLReplacement);
		}
		return s;
	}

	public static String getJavascriptCode(String textAreaName) {
		StringBuilder s = new StringBuilder();

		s.append("function refreshSyntaxButtons_" + textAreaName + "() {\n");
		s.append("	var currentSyntaxList_" + textAreaName + " = document.getElementById(\"" + textAreaName + "_syntaxes\");\n");
		s.append("	currentSyntax_" + textAreaName + " = currentSyntaxList_" + textAreaName + " = currentSyntaxList_" + textAreaName +
				".options[currentSyntaxList_" + textAreaName + ".selectedIndex].value;\n");
		s.append("	var buttonsDiv = document.getElementById(\"" + textAreaName + "_buttons\");\n" +
				"	while(buttonsDiv.hasChildNodes()) { buttonsDiv.removeChild(buttonsDiv.firstChild); }\n");

		for (Syntaxes e : ContentSyntax.Syntaxes.values()) {
			s.append("	if(currentSyntax_" + textAreaName + " == \"" + e.toString() + "\") {\n");

			int i = 0;
			try {
				ContentSyntax eCS = (ContentSyntax) Class.forName("plugins.floghelper.contentsyntax." + e.toString()).newInstance();
				for (SyntaxElement eSE : eCS.syntaxElements) {
					if (!eSE.isMajor) {
						continue;
					}

					s.append("		var button" + i + " = document.createElement(\"input\");\n" +
							"		button" + i + ".setAttribute(\"type\", \"button\");\n" +
							"		button" + i + ".setAttribute(\"value\", \"" + eSE.name + "\");\n" +
							"		button" + i + ".addEventListener(\"click\", function(event){ addSomething(\"" + eSE.beginThing.replace("\"", "\\\"") + "\", \"" + eSE.endThing.replace("\"", "\\\"") +
							"\", \"" + textAreaName + "\"); }, false);\n" +
							"		buttonsDiv.appendChild(button" + i + ")\n");
					++i;
				}
			} catch (InstantiationException ex) {
				Logger.error(ContentSyntax.class, "Could NOT instanciate ContentSyntax " + e.toString());
			} catch (IllegalAccessException ex) {
				Logger.error(ContentSyntax.class, "Could NOT instanciate ContentSyntax " + e.toString());
			} catch (ClassNotFoundException ex) {
				Logger.error(ContentSyntax.class, "Could NOT instanciate ContentSyntax " + e.toString());
			}

			s.append("	}\n");
		}

		return s.append("}\n").toString();
	}

	public static HTMLNode addJavascriptEditbox(HTMLNode parentForm, String textAreaName, String defaultSelectedValue, String defaultContent, String textAreaLabel) {
		parentForm.addChild("script", new String[]{"type", "src"}, new String[]{"text/javascript",
					FlogHelperToadlet.BASE_URI + JavascriptFactoryToadlet.MY_URI + "EditBox.js"}, " ");

		parentForm.addChild("script", new String[]{"type", "src"}, new String[]{"text/javascript",
					FlogHelperToadlet.BASE_URI + JavascriptFactoryToadlet.MY_URI + "RefreshSyntaxButtons-" + textAreaName + ".js"}, " ");

		parentForm = parentForm.addChild("p");
		parentForm.addChild("#", textAreaLabel);
		parentForm.addChild("br");

		final HTMLNode syntaxesList = parentForm.addChild(
				"select", new String[]{"id", "name", "onchange"}, new String[]{textAreaName + "_syntaxes", textAreaName + "_syntaxes",
					"refreshSyntaxButtons_" + textAreaName + "();"});
		for (Syntaxes e : ContentSyntax.Syntaxes.values()) {
			HTMLNode syntaxListElement = syntaxesList.addChild("option", "value", e.toString(), e.toString());
			if (defaultSelectedValue != null && defaultSelectedValue.equals(e.toString())) {
				syntaxListElement.addAttribute("selected", "selected");
			}
		}

		parentForm.addChild("br").addChild("span", new String[]{"id", "style"}, new String[]{textAreaName + "_buttons", "display: inline-block; width: 650px;"}, "&nbsp;");
		parentForm.addChild("br");
		parentForm.addChild("textarea", new String[]{"rows", "cols", "name", "id"},
				new String[]{"12", "80", textAreaName, textAreaName}, defaultContent);

		parentForm.addChild("script", "type", "text/javascript", "refreshSyntaxButtons_" + textAreaName + "();");

		return parentForm;
	}
}