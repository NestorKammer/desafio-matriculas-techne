import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly mensagem = signal<string | null>(null);
  readonly tipo = signal<'ok' | 'erro'>('ok');

  sucesso(texto: string): void {
    this.tipo.set('ok');
    this.mensagem.set(texto);
  }

  erro(texto: string): void {
    this.tipo.set('erro');
    this.mensagem.set(texto);
  }

  limpar(): void {
    this.mensagem.set(null);
  }
}
