package oleksii.filonov.gui;

import java.io.File;

import javax.swing.JFileChooser;

public class MainFileChooser extends JFileChooser {

    private static final long serialVersionUID = 1L;

    private File bodyIdFile;

    private File campaignFile;

    private File linkedBodyFile;

    public MainFileChooser(final File defaultDirectory) {
        super(defaultDirectory);
    }

    public File getLinkedBodyFile() {
        return this.linkedBodyFile;
    }

    public void setLinkedBodyFile(final File linkedBodyFile) {
        this.linkedBodyFile = linkedBodyFile;
    }

    public File getBodyIdFile() {
        return this.bodyIdFile;
    }

    public void setBodyIdFile(final File bodyIdFile) {
        this.bodyIdFile = bodyIdFile;
    }

    public File getCampaignFile() {
        return this.campaignFile;
    }

    public void setCampaignFile(final File campaignFile) {
        this.campaignFile = campaignFile;
    }

}
