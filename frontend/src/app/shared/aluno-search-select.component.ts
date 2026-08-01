import {
  Component,
  ElementRef,
  HostListener,
  Input,
  OnChanges,
  SimpleChanges,
  forwardRef,
  inject
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { Aluno } from '../core/models';

@Component({
  selector: 'app-aluno-search-select',
  standalone: true,
  imports: [NgFor, NgIf],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AlunoSearchSelectComponent),
      multi: true
    }
  ],
  templateUrl: './aluno-search-select.component.html',
  styleUrl: './aluno-search-select.component.css'
})
export class AlunoSearchSelectComponent implements ControlValueAccessor, OnChanges {
  private readonly host = inject(ElementRef<HTMLElement>);

  @Input() alunos: Aluno[] = [];
  @Input() allowEmpty = false;
  @Input() emptyLabel = 'Todos';
  @Input() placeholder = 'Digite RA ou nome';

  aberto = false;
  termo = '';
  private valor = 0;
  private onChange: (value: number) => void = () => undefined;
  private onTouched: () => void = () => undefined;
  disabled = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['alunos'] && !this.aberto) {
      this.sincronizarTermo();
    }
  }

  get alunosOrdenados(): Aluno[] {
    return [...this.alunos].sort((a, b) => {
      const porNome = a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' });
      if (porNome !== 0) {
        return porNome;
      }
      return a.ra.localeCompare(b.ra, 'pt-BR', { numeric: true, sensitivity: 'base' });
    });
  }

  get opcoes(): Aluno[] {
    const q = this.termo.trim().toLocaleLowerCase('pt-BR');
    const base = this.alunosOrdenados;
    if (!q) {
      return base;
    }
    return base.filter(
      a =>
        a.ra.toLocaleLowerCase('pt-BR').includes(q) ||
        a.nome.toLocaleLowerCase('pt-BR').includes(q)
    );
  }

  writeValue(value: number | null): void {
    this.valor = value ?? 0;
    this.sincronizarTermo();
  }

  registerOnChange(fn: (value: number) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  abrir(): void {
    if (this.disabled) {
      return;
    }
    this.aberto = true;
    if (this.valor) {
      this.termo = '';
    }
  }

  fechar(): void {
    this.aberto = false;
    this.sincronizarTermo();
    this.onTouched();
  }

  onInput(event: Event): void {
    if (this.disabled) {
      return;
    }
    this.termo = (event.target as HTMLInputElement).value;
    this.aberto = true;
    if (this.valor) {
      this.valor = 0;
      this.onChange(0);
    }
  }

  selecionar(aluno: Aluno): void {
    this.valor = aluno.id;
    this.termo = this.rotulo(aluno);
    this.aberto = false;
    this.onChange(aluno.id);
    this.onTouched();
  }

  selecionarVazio(): void {
    this.valor = 0;
    this.termo = '';
    this.aberto = false;
    this.onChange(0);
    this.onTouched();
  }

  rotulo(aluno: Aluno): string {
    return `${aluno.ra} - ${aluno.nome}`;
  }

  estaSelecionado(id: number): boolean {
    return this.valor === id;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.host.nativeElement.contains(event.target as Node)) {
      if (this.aberto) {
        this.fechar();
      }
    }
  }

  private sincronizarTermo(): void {
    if (!this.valor) {
      this.termo = '';
      return;
    }
    const aluno = this.alunos.find(a => a.id === this.valor);
    this.termo = aluno ? this.rotulo(aluno) : '';
  }
}
