#!/bin/bash

tmux new-session -d -s work
tmux rename-window -t work:0 'main'
#tmux send-keys -t work:0 'ls -al' Enter

tmux split-window -v -p 20
tmux select-pane -t 0
tmux split-window -h

tmux select-pane -t 0
tmux send-keys 'ls -l' Enter

tmux select-pane -t 1
tmux send-keys 'ls -al' Enter

tmux select-pane -t 2
#tmux select-window -t work:1
tmux attach-session -t work
