import { Disposable, Webview, WebviewPanel, window, Uri, ViewColumn } from "vscode";
import { getUri } from "../utilities/getUri";
import { getNonce } from "../utilities/getNonce";
import * as vscode from "vscode";
import { NodeDependenciesProvider } from "../TreeDataProvider/NodeDependenciesProvider";
import { Trouble } from "../TreeDataProvider/MyTroubleListProvider";
import { v4 as uuidv4 } from "uuid";
import { TROUBLE_SHOOTING_TYPE } from "../extension";

type TroubleShootingType = typeof TROUBLE_SHOOTING_TYPE[keyof typeof TROUBLE_SHOOTING_TYPE];

/**
 * This class manages the state and behavior of HelloWorld webview panels.
 *
 * It contains all the data and methods for:
 *
 * - Creating and rendering HelloWorld webview panels
 * - Properly cleaning up and disposing of webview resources when the panel is closed
 * - Setting the HTML (and by proxy CSS/JavaScript) content of the webview panel
 * - Setting message listeners so data can be passed between the webview and extension
 */
export class TroubleShotPanel {
  public static currentPanel: TroubleShotPanel | undefined;
  private readonly _panel: WebviewPanel;
  private _disposables: Disposable[] = [];
  private readonly _sessionId: number;
  private readonly _troubleShootingType: TroubleShootingType;
  private readonly _globalState: vscode.Memento;
  private readonly _troubleId?: string;

  /**
   * The HelloWorldPanel class private constructor (called only from the render method).
   *
   * @param panel A reference to the webview panel
   * @param extensionUri The URI of the directory containing the extension
   */
  private constructor(
    panel: WebviewPanel,
    extensionUri: Uri,
    sessionId: number,
    troubleShootingType: TroubleShootingType,
    globalState: vscode.Memento,
    troubleId?: string
  ) {
    this._panel = panel;

    // Set an event listener to listen for when the panel is disposed (i.e. when the user closes
    // the panel or when the panel is closed programmatically)
    this._panel.onDidDispose(() => this.dispose(), null, this._disposables);

    // Set the HTML content for the webview panel
    this._panel.webview.html = this._getWebviewContent(this._panel.webview, extensionUri);

    // Set an event listener to listen for messages passed from the webview context
    this._setWebviewMessageListener(this._panel.webview);

    // Set the isLogin property
    this._sessionId = sessionId;

    this._troubleShootingType = troubleShootingType;

    this._globalState = globalState;

    this._troubleId = troubleShootingType === 1 ? troubleId : undefined;
  }

  /**
   * Renders the current webview panel if it exists otherwise a new webview panel
   * will be created and displayed.
   *
   * @param extensionUri The URI of the directory containing the extension.
   */
  public static render(
    extensionUri: Uri,
    sessionId: number,
    troubleShootingType: TroubleShootingType,
    globalState: vscode.Memento,
    troubleId?: string
  ) {
    if (TroubleShotPanel.currentPanel) {
      // If the webview panel already exists reveal it
      TroubleShotPanel.currentPanel._panel.reveal(ViewColumn.One);
    } else {
      // If a webview panel does not already exist create and show a new one
      const panel = window.createWebviewPanel(
        // Panel view type
        "createTroubleShooting",
        // Panel title
        "Create Trouble Shooting",
        // The editor column the panel should be displayed in
        ViewColumn.One,
        // Extra panel configurations
        {
          // Enable JavaScript in the webview
          enableScripts: true,
          // Restrict the webview to only load resources from the `out` and `webview-ui/build` directories
          localResourceRoots: [
            Uri.joinPath(extensionUri, "out"),
            Uri.joinPath(extensionUri, "webview-ui/build"),
          ],
        }
      );

      TroubleShotPanel.currentPanel = new TroubleShotPanel(
        panel,
        extensionUri,
        sessionId,
        troubleShootingType,
        globalState,
        troubleId
      );
    }
  }

  /**
   * Cleans up and disposes of webview resources when the webview panel is closed.
   */
  public dispose() {
    TroubleShotPanel.currentPanel = undefined;

    // Dispose of the current webview panel
    this._panel.dispose();

    // Dispose of all disposables (i.e. commands) for the current webview panel
    while (this._disposables.length) {
      const disposable = this._disposables.pop();
      if (disposable) {
        disposable.dispose();
      }
    }
  }

  /**
   * Defines and returns the HTML that should be rendered within the webview panel.
   *
   * @remarks This is also the place where references to the React webview build files
   * are created and inserted into the webview HTML.
   *
   * @param webview A reference to the extension webview
   * @param extensionUri The URI of the directory containing the extension
   * @returns A template string literal containing the HTML that should be
   * rendered within the webview panel
   */
  private _getWebviewContent(webview: Webview, extensionUri: Uri) {
    // The CSS file from the React build output
    const stylesUri = getUri(webview, extensionUri, ["webview-ui", "build", "assets", "index.css"]);
    // The JS file from the React build output
    const scriptUri = getUri(webview, extensionUri, ["webview-ui", "build", "assets", "index.js"]);

    const nonce = getNonce();

    // Tip: Install the es6-string-html VS Code extension to enable code highlighting below
    return /*html*/ `
      <!DOCTYPE html>
      <html lang="en">
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <meta http-equiv="Content-Security-Policy" content="default-src 'none'; style-src ${webview.cspSource}; script-src 'nonce-${nonce}';">
          <link rel="stylesheet" type="text/css" href="${stylesUri}">
          <title>Trouble Shot</title>
        </head>
        <body>
          <div id="root"></div>
          <script type="module" nonce="${nonce}" src="${scriptUri}"></script>
        </body>
      </html>
    `;
  }

  /**
   * Sets up an event listener to listen for messages passed from the webview context and
   * executes code based on the message that is recieved.
   *
   * @param webview A reference to the extension webview
   * @param context A reference to the extension context
   */
  private _setWebviewMessageListener(webview: Webview) {
    webview.onDidReceiveMessage(
      async (message: any) => {
        const command = message.command;

        switch (command) {
          case "getInitialStatus":
            if (this._troubleShootingType === TROUBLE_SHOOTING_TYPE.TROUBLE) {
              webview.postMessage({
                command: "getInitialStatus",
                troubleShootingType: this._troubleShootingType,
                sessionId: this._sessionId,
                defaultSkills: NodeDependenciesProvider.allDependencies,
              });
            }
            if (this._troubleShootingType === TROUBLE_SHOOTING_TYPE.SOLUTION) {
              webview.postMessage({
                command: "getInitialStatus",
                troubleShootingType: this._troubleShootingType,
                sessionId: this._sessionId,
                troubleId: this._troubleId,
              });
            }
            if (this._troubleShootingType === TROUBLE_SHOOTING_TYPE.LOGIN_FORM) {
              webview.postMessage({
                command: "getInitialStatus",
                troubleShootingType: this._troubleShootingType,
              });
            }
            return;
          case "showMessage":
            if (message.type === "error") {
              vscode.window.showErrorMessage(message.content);
            }
            if (message.type === "info") {
              vscode.window.showInformationMessage(message.content);
            }
            return;
          case "addTrouble":
            try {
              const newTrouble = new Trouble(
                message.articleInfo.title,
                message.articleInfo.createTime,
                "my",
                message.articleInfo.content,
                uuidv4(),
                "unSolved"
              );
              const prevTroubleList = this._globalState.get<Trouble[]>("troubleList");
              await this._globalState.update(
                "troubleList",
                prevTroubleList ? [...prevTroubleList, newTrouble] : [newTrouble]
              );
              vscode.commands.executeCommand("refresh.trouble");
              vscode.window.showInformationMessage("Added to trouble list!");
              this.dispose();
            } catch (error) {
              vscode.window.showErrorMessage("Failed to add to trouble list!");
            }
            return;
          case "solveTrouble":
            try {
              const prevTroubleList = this._globalState.get<Trouble[]>("troubleList");
              const newTroubleList = prevTroubleList?.map((trouble) => {
                if (trouble.id === message.articleInfo.troubleId) {
                  trouble.content += "\n\n";
                  trouble.content += message.articleInfo.content;
                  trouble.contextValue = "solved";
                }
                return trouble;
              });
              await this._globalState.update("troubleList", newTroubleList);
              vscode.commands.executeCommand("refresh.trouble");
              vscode.window.showInformationMessage("Trouble solved!");
              this.dispose();
            } catch (error) {
              vscode.window.showErrorMessage("Failed to solve!");
            }
            return;
          // Add more switch case statements here as more webview message commands
          // are created within the webview context (i.e. inside media/main.js)
        }
      },
      undefined,
      this._disposables
    );
  }
}
