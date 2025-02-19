package dev.onlooker.gui.clickguis.sidegui.forms.impl;

import dev.onlooker.Client;
import dev.onlooker.gui.clickguis.sidegui.forms.Form;
import dev.onlooker.gui.clickguis.sidegui.utils.ActionButton;
import dev.onlooker.gui.clickguis.sidegui.utils.DropdownObject;
import dev.onlooker.utils.client.cloud.data.CloudData;
import dev.onlooker.utils.font.FontUtil;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.misc.Multithreading;
import dev.onlooker.utils.objects.TextField;
import dev.onlooker.utils.render.ColorUtil;
import dev.onlooker.utils.render.RoundedUtil;

import java.awt.*;

public class EditForm extends Form {
    private boolean script;
    private CloudData data;


    private final ActionButton updateButton = new ActionButton("Update");
    private final ActionButton deleteButton = new ActionButton("Delete");

    private final DropdownObject scriptFiles = new DropdownObject("File");

    private final TextField descriptionField = new TextField(tenacityFont18);

    private final String type;

    public EditForm(String type) {
        super("Edit " + type);
        this.type = type;
        setWidth(404);
        setHeight(175);
    }

    @Override
    public void initGui() {

    }

    private boolean error = false;

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        descriptionField.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        float infoX = getX() + tenacityBoldFont40.getStringWidth(getTitle()) + 15;
        float infoY = getY() + 3 + tenacityFont16.getMiddleOfBox(tenacityBoldFont40.getHeight()) + 2;

        tenacityFont16.drawString("Choose to either update or delete your " + type.toLowerCase(),
                infoX, infoY, ColorUtil.applyOpacity(getTextColor(), .5f));

        tenacityBoldFont16.drawString(data.getName(), getX() + 5, getY() + 5.5f + tenacityBoldFont40.getHeight(), getAccentColor());

        float totalSpacing = getSpacing() * 3;
        float updateWidth = (getWidth() - totalSpacing) * .7f;
        float deleteWidth = (getWidth() - totalSpacing) * .3f;

        float updateX = getX() + getSpacing();
        float updateY = getY() + 40;
        float updateHeight = getHeight() - (40 + getSpacing());
        //Update background
        RoundedUtil.drawRound(getX() + getSpacing(), getY() + 40, updateWidth, updateHeight, 5, ColorUtil.tripleColor(29, getAlpha()));

        tenacityBoldFont26.drawString("Update", updateX + 5, updateY + 3, getTextColor());


        Color noColor = ColorUtil.applyOpacity(Color.WHITE, 0);
        descriptionField.setWidth(updateWidth - 10);
        descriptionField.setXPosition(updateX + 5);
        descriptionField.setYPosition(updateY + (script ? 65 : 45));
        descriptionField.setTextAlpha(getAlpha());
        descriptionField.setMaxStringLength(210);
        descriptionField.setHeight(script ? 18 : 20);
        descriptionField.setFont(tenacityFont16);
        descriptionField.setFill(ColorUtil.tripleColor(17, getAlpha()));
        descriptionField.setOutline(noColor);
        descriptionField.drawTextBox();

        tenacityFont18.drawString("Description", descriptionField.getXPosition(),
                descriptionField.getYPosition() - (tenacityFont18.getHeight() + 5), getTextColor());

        updateButton.setBypass(true);
        updateButton.setWidth(70);
        updateButton.setHeight(15);
        updateButton.setBold(true);
        updateButton.setX(updateX + updateWidth / 2f - updateButton.getWidth() / 2f);
        updateButton.setY(updateY + updateHeight - (updateButton.getHeight() + getSpacing()));
        updateButton.setAlpha(getAlpha());
        updateButton.setClickAction(() -> {
            getUploadAction().accept(scriptFiles.getSelection(), descriptionField.getText());
            Client.INSTANCE.getSideGui().displayForm(null);
        });
        updateButton.drawScreen(mouseX, mouseY);


        if (error) {
            tenacityFont16.drawCenteredStringWithShadow("Error please fill out the required fields",
                    updateButton.getX() + updateButton.getWidth() / 2f, updateButton.getY() - (tenacityFont16.getHeight() + 5),
                    Client.INSTANCE.getSideGui().getRedBadColor().getRGB());
        }

        if (script) {
            tenacityFont16.drawString("Update the script with one of your local scripts",
                    updateX + 10 + tenacityBoldFont26.getStringWidth("Update"),
                    updateY + 4 + tenacityFont16.getMiddleOfBox(tenacityBoldFont26.getHeight()), ColorUtil.applyOpacity(getTextColor(), .5f));

            scriptFiles.setWidth(160);
            scriptFiles.setHeight(18);
            scriptFiles.setY(updateY + 25);
            scriptFiles.setX(updateX + updateWidth / 2f - scriptFiles.getWidth() / 2f);
            scriptFiles.setAlpha(getAlpha());
            scriptFiles.setBypass(true);
            scriptFiles.setAccentColor(getAccentColor());
            scriptFiles.drawScreen(mouseX, mouseY);

        } else {
            tenacityFont16.drawString("Update the config with your current settings",
                    updateX + 10 + tenacityBoldFont26.getStringWidth("Update"),
                    updateY + 4 + tenacityFont16.getMiddleOfBox(tenacityBoldFont26.getHeight()), ColorUtil.applyOpacity(getTextColor(), .5f));
        }


        float deleteX = getX() + (getSpacing() * 2) + updateWidth;
        float deleteY = getY() + 40;
        float deleteHeight = getHeight() - (40 + getSpacing());
        //Delete background
        RoundedUtil.drawRound(deleteX, deleteY, deleteWidth, deleteHeight, 5, new Color(251, 14, 14, (int) (255 * (.22f * getAlpha()))));


        tenacityBoldFont26.drawString("Delete", deleteX + 5, deleteY + 3, getTextColor());
        iconFont26.drawString(FontUtil.TRASH, deleteX + 10 + tenacityBoldFont26.getStringWidth("Delete"), deleteY + 4.5f, getTextColor());

        tenacityFont16.drawString("Delete the " + type.toLowerCase() + " from the", deleteX + 5, deleteY + 22, ColorUtil.applyOpacity(getTextColor(), .5f));
        tenacityFont16.drawString("cloud", deleteX + 5, deleteY + 22 + tenacityFont16.getHeight() + 3, ColorUtil.applyOpacity(getTextColor(), .5f));

        deleteButton.setWidth(70);
        deleteButton.setX(deleteX + deleteWidth / 2f - deleteButton.getWidth() / 2f);
        deleteButton.setHeight(15);
        deleteButton.setY(deleteY + deleteHeight - (deleteButton.getHeight() + getSpacing()));
        deleteButton.setAlpha(getAlpha());
        deleteButton.setBypass(true);
        deleteButton.setBold(true);
        deleteButton.setColor(Client.INSTANCE.getSideGui().getRedBadColor());
        deleteButton.setClickAction(() -> {
            Multithreading.runAsync(() -> {
                Client.INSTANCE.getSideGui().displayForm(null);
                Client.INSTANCE.getCloudDataManager().refreshData();
            });
        });
        deleteButton.drawScreen(mouseX, mouseY);


    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        scriptFiles.mouseClicked(mouseX, mouseY, button);
        deleteButton.mouseClicked(mouseX, mouseY, button);
        if (scriptFiles.isClosed()) {
            super.mouseClicked(mouseX, mouseY, button);
            descriptionField.mouseClicked(mouseX, mouseY, button);

            if (HoveringUtil.isHovering(updateButton.getX(), updateButton.getY(), updateButton.getWidth(), updateButton.getHeight(), mouseX, mouseY)) {
                String descriptionText = descriptionField.getText();
                String[] descArray = descriptionText.split(" ");
                boolean descriptionFilter = descriptionText.length() > 35 && !(descArray.length > 1);
                if (descriptionField.getText().isEmpty() || descriptionFilter) {
                    error = true;
                } else {
                    error = false;
                    updateButton.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    @Override
    public void clear() {

    }


    public void setup(CloudData data, boolean script) {
        this.script = script;
        this.data = data;
        descriptionField.setMaxStringLength(210);
        descriptionField.setText(data.getDescription());
    }
}
