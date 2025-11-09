section .data
http_header:
    db "HTTP/1.0 200 OK", 13, 10, 13, 10
http_header_len equ $ - http_header

section .bss
buffer      resb 1024
file_path   resb 256

section .text
global _start

_start:
    mov rax, 41
    mov rdi, 2
    mov rsi, 1
    xor rdx, rdx
    syscall
    mov r15, rax

    xor rax, rax
    push rax
    mov rbx, 0x0
    push rbx
    mov word [rsp-2], 0x5000
    mov word [rsp-4], 2
    sub rsp, 4
    lea rsi, [rsp]
    mov rdi, r15
    mov rdx, 16
    mov rax, 49
    syscall

    xor rsi, rsi
    mov rdi, r15
    mov rax, 50
    syscall

.accept_loop:
    mov rdi, r15
    xor rsi, rsi
    xor rdx, rdx
    mov rax, 43
    syscall
    mov r12, rax

    mov rax, 57
    syscall
    cmp rax, 0
    je .child

    mov rdi, r12
    mov rax, 3
    syscall
    jmp .accept_loop

.child:
    mov rdi, r15
    mov rax, 3
    syscall

    mov rdi, r12
    lea rsi, [rel buffer]
    mov rdx, 1024
    xor rax, rax
    syscall
    mov r14, rax

    lea rsi, [rel buffer]
    mov eax, dword [rsi]
    cmp eax, 0x20544547
    je .handle_get
    cmp eax, 0x54534f50
    je .handle_post
    jmp .exit_child

.handle_post:
    lea rsi, [rel buffer + 5]
    lea rdi, [rel file_path]
.get_path_post:
    mov al, byte [rsi]
    cmp al, ' '
    je .done_path_post
    mov [rdi], al
    inc rsi
    inc rdi
    jmp .get_path_post
.done_path_post:
    mov byte [rdi], 0

    mov rax, 2
    lea rdi, [rel file_path]
    mov rsi, 65
    mov rdx, 0x1ff
    syscall
    mov r13, rax
    cmp rax, 0
    js .exit_child

    lea rsi, [rel buffer]
.find_body:
    lea rbx, [rel buffer + r14]
    cmp rsi, rbx
    jae .no_body
    cmp byte [rsi], 13
    jne .inc_body
    cmp byte [rsi+1], 10
    jne .inc_body
    cmp byte [rsi+2], 13
    jne .inc_body
    cmp byte [rsi+3], 10
    jne .inc_body
    add rsi, 4
    jmp .body_found
.inc_body:
    inc rsi
    jmp .find_body
.no_body:
    lea rsi, [rel buffer + r14]
.body_found:
    lea rcx, [rel buffer]
    mov rbx, rsi
    sub rbx, rcx
    mov rdx, r14
    sub rdx, rbx

    mov rax, 1
    mov rdi, r13
    syscall

    mov rax, 3
    mov rdi, r13
    syscall
    jmp .write_response

.handle_get:
    lea rsi, [rel buffer + 4]
    lea rdi, [rel file_path]
.get_path_get:
    mov al, byte [rsi]
    cmp al, ' '
    je .done_path_get
    mov [rdi], al
    inc rsi
    inc rdi
    jmp .get_path_get
.done_path_get:
    mov byte [rdi], 0

    mov rax, 2
    lea rdi, [rel file_path]
    xor rsi, rsi
    xor rdx, rdx
    syscall
    mov r13, rax
    cmp rax, 0
    js .write_response

    mov rdi, r13
    lea rsi, [rel buffer]
    mov rdx, 1024
    xor rax, rax
    syscall
    mov r14, rax

    mov rdi, r13
    mov rax, 3
    syscall

.write_response:
    mov rdi, r12
    mov rax, 1
    lea rsi, [rel http_header]
    mov rdx, http_header_len
    syscall

    mov rdi, r12
    lea rsi, [rel buffer]
    mov rdx, r14
    mov rax, 1
    syscall

    mov rdi, r12
    mov rax, 3
    syscall

.exit_child:
    mov rax, 60
    xor rdi, rdi
    syscall
