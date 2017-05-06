package wci.ide.handler.add;

import wci.ide.AddFrame;
import wci.ide.EditorFrame;

public interface AddHandler {
    void afterAdd(EditorFrame editorFrame, AddFrame addFrame, Object data);
}
