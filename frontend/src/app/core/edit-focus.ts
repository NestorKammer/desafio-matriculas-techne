/** Rola ate o bloco de edicao e foca o primeiro campo editavel. */
export function focarBlocoEdicao(el: HTMLElement | null | undefined): void {
  if (!el) {
    return;
  }
  requestAnimationFrame(() => {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    const campo = el.querySelector<HTMLElement>(
      'input:not([disabled]):not([type="hidden"]), select:not([disabled]), textarea:not([disabled])'
    );
    (campo ?? el).focus({ preventScroll: true });
  });
}
