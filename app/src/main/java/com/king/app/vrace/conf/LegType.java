package com.king.app.vrace.conf;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/6/7 17:23
 */
public enum LegType {
    EL, // elimination leg
    NEL, // non-elimination leg
    DLL, // double-length leg
    DEL, // double-elimination leg
    START_LINE, // start line without task
    FINAL, // final leg
    START_LINE_TASK, // start line with task
    START_LINE_EL // start line with task and elimination
}
