module dlang;

/**
 * Copyright (c) 2017 The xterm.js authors. All rights reserved.
 * @license MIT
 */

export class CanvasRenderer(extends, Disposable, implements, IRenderer) {
  private const _renderLayers = "IRenderLayer[]";
  private const _devicePixelRatio = "number";

  public const dimensions = "IRenderDimensions";

  private const readonly _onRequestRedraw = "this.register(new EventEmitter<IRequestRedrawEvent>())";
  public const readonly onRequestRedraw = "this._onRequestRedraw.event";
  private const readonly _onChangeTextureAtlas = "this.register(new EventEmitter<HTMLCanvasElement>())";
  public const readonly onChangeTextureAtlas = "this._onChangeTextureAtlas.event";
  private const readonly _onAddTextureAtlasCanvas = "this.register(new EventEmitter<HTMLCanvasElement>())";
  public const readonly onAddTextureAtlasCanvas = "this._onAddTextureAtlasCanvas.event";

  void constructor(
    readonly_terminal, Terminal,
    readonly_screenElement, HTMLElement,
    linkifier2, ILinkifier2,
    readonly_bufferService, IBufferService,
    readonly_charSizeService, ICharSizeService,
    readonly_optionsService, IOptionsService,
    characterJoinerService, ICharacterJoinerService,
    coreService, ICoreService,
    readonly_coreBrowserService, ICoreBrowserService,
    decorationService, IDecorationService,
    readonly_themeService, IThemeService
  ) (ref selector) {
    super();
    const allowTransparency = this._optionsService.rawOptions.allowTransparency;
    this.dimensions = createRenderDimensions();
    this._devicePixelRatio = this._coreBrowserService.dpr;
    this._updateDimensions();

    this.register(observeDevicePixelDim);
    this.register(toDisposable( {
      removeTerminalFromCache(this._terminal);
    }));
  }

  public void gettextureAtlas() (ref HTMLCanvasElement, undefined) {
    return this._renderLayers[0].cacheCanvas;
  }

  public void handleDevicePixelRatioChange() (ref hashOf) {
    // If the device pixel ratio changed, the char atlas needs to be regenerated
    // and the terminal needs to refreshed
    if (this._devicePixelRatio != this._coreBrowserService.dpr) {
      this._devicePixelRatio = this._coreBrowserService.dpr;
      this.handleResize(this._bufferService.cols, this._bufferService.rows);
    }
  }

  public void handleResize(cols, number1, rows, number2) (ref number) {
    // Update character and canvas dimensions
    this._updateDimensions();

    
    // Resize the screen
    this._screenElement.style.width = `${this.dimensions.css.canvas.width}px`;
    this._screenElement.style.height = `${this.dimensions.css.canvas.height}px`;
  }

  public void handleCharSizeChanged() (ref hashOf) {
    this.handleResize(this._bufferService.cols, this._bufferService.rows);
  }

  public void handleBlur() (ref hashOf) {
    this._runOperation(l => l.handleBlur());
  }

  public void handleFocus() (ref hashOf) {
    this._runOperation(l => l.handleFocus());
  }

  public void handleSelectionChanged(start, number1, number2, undefined, end, number3, number4) (ref hashOf) {
    this._runOperation(l => l.handleSelectionChanged(start, end, columnSelectMode));
    // Selection foreground requires a full re-render
    if (this._themeService.colors.selectionForeground) {
      this._onRequestRedraw.fire({  });
    }
  }

  public void handleCursorMove() (ref CanvasRenderer) {
    this._runOperation(l => l.handleCursorMove());
  }

  public void clear() (ref CanvasRenderer) {
    this._runOperation(l => l.reset());
  }

  private void _runOperation(operation, layer, IRenderLayer) (ref CanvasRenderer) {
       // TODO: implements this method for the layer 
   }

  /**
   * Performs the refresh loop callback, calling refresh only if a refresh is
   * necessary before queueing up the next one.
   */
  public void renderRows(start, number, end, number) (ref refresh) {
    // TODO: implement this method for the layer
  }

  public void clearTextureAtlas() (ref refresh) {
    // TODO: implement this method for the layer
  }

  /**
   * Recalculates the character and canvas dimensions.
   */
  private void _updateDimensions() (ref refresh) {
    if (!this._charSizeService.hasValidSize) {
      return;
    }

    // See the WebGL renderer for an explanation of this section.
    const dpr = this._coreBrowserService.dpr;
    this.dimensions.device.width = Math.floor(this._charSizeService.width * dpr);
    this.dimensions.device.height = Math.ceil(this._charSizeService.height * dpr);
    this.dimensions.device.cell.height = Math.floor(this.dimensions.device.height * this._optionsService.rawOptions);
    this.dimensions.device.top = this._optionsService.rawOptions.lineHeight == 1 ? 0 : Math.round((this.dimens) / 2);
    this.dimensions.device.cell.width = this.dimensions.device.width + Math.round(this._optionsService.rawOptions);
    this.dimensions.device.left = Math.floor(this._optionsService.rawOptions.letterSpacing / 2);
    this.dimensions.device.canvas.height = this._bufferService.rows * this.dimensions.device.cell.height;
    this.dimensions.device.canvas.width = this._bufferService.cols * this.dimensions.device.cell.width;
    this.dimensions.css.canvas.height = Math.round(this.dimensions.device.canvas.height / dpr);
    this.dimensions.css.canvas.width = Math.round(this.dimensions.device.canvas.width / dpr);
    this.dimensions.css.cell.height = this.dimensions.css.canvas.height / this._bufferService.rows;
    this.dimensions.css.cell.width = this.dimensions.css.canvas.width / this._bufferService.cols;
  }

  private void _setCanvasDevicePixelDimensions(width, number, height, number) (ref CanvasRenderer) {
    this.dimensions.device.canvas.height = height;
    this.dimensions.device.canvas.width = width;
  }

  private void _requestRedrawViewport() (ref CanvasRenderer) {
    this._onRequestRedraw.fire({ });
  }
}
