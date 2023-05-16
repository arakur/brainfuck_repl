# Brainfuck REPL

A simple CUI tool for interacting with Brainfuck interpreter written in Scala3.

## Installation

`git clone` this repository and run with `sbt`.

Scala version = 3.2.2

## Commands

- `exec <code>` or `<code>` : execute brainfuck code.
- `load <path>` : load external brainfuck source and run.
- `state` : print current state.
- `clear` : clear current state.
- `discard` : clear input stream in state (used in `set discard_input false`).
- `set <option> [<arg>]` : set option.
- `get <option>` : get current value of option.
- `exit` : exit REPL.

## Options

- `run_step` : if `true`, REPL runs code step-by-step. default: `false`.
- `discard_input` : if `true`, REPL discards input stream when a execution finished. default: `true`.
- `print_state` : if `true`, REPL prints current state when a execution finished. default: `true`.
- `auto_clear` : if `true`, REPL clears current state when a execution finished. default: `false`.
- `escape_input` : if `true`, one can input string with escape sequences (only `\00` ~ `\7F` and `\\`). default: `true`.

## License

MIT
