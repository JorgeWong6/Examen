
;; Define un macro usando DefMacro. Su macro debe mostrar una lista que ser evaluada como un codigo "Clojure".

;;Este macro es algo similar como si escribieras(reversa "Hola Mundo".) (DefMacro mi-primer-macro[]
  
;;Inspeccionar el resultado de un macro usando MacroExpand o MacroExpand- 

1. 

;; Tenga en cuenta que la llamada debe ser citada.
(macroexpand '(mi-primer-macro))
;; -> (#<core$reverse clojure.core$reverse@xxxxxxxx> "Hola Mundo")

;; Puede evaluar el resultado de macroexpand directamente:
(evaluar (macroexpand '(mi-primer-macro)))
; -> (\d \l \o \r \W \space \o \l \l \e \H)

;; Pero usted debe utilizar este más sucinto,Funcion-como sintaxys:
(mi-primer-macro)  ; -> (\d \l \o \r \W \space \o \l \l \e \H)

;; Puedes hacer las cosas más fáciles sobre ti usando la sintaxis más sucinta de la sintaxis 
;; Para crear listas en tu macro:
(defmacro mi-primer-citado-macro []
  '(reversa "Hola Mundo"))

(macroexpand '(mi-primer-citado-macro))
;; -> (reversa "Hola Mundo")
;; Observe que el reverso ya no es un objeto de función, sino un símbolo.

;; Macros pueden tomar argumentos.
(defmacro inc2 [arg]
  (list + 2 arg))

(inc2 2) ; -> 4

;; 
Pero, si intenta hacer esto con una lista citada, obtendrá un error, porque;
;El argumento será citado también. Para evitar esto, clojure proporciona una;
;; madera de citar macros: `. dentro `, tu puedes usar ~ Para llegar al ámbito exterior.
(defmacro inc2-quoted [arg]
  `(+ 2 ~arg))

(inc2-quoted 2)

;; 
Puede usar los args de desestructuración habituales. Expandir variables de lista utilizando ~@
(defmacro unless [arg & body]
  `(if (not ~arg)
     (do ~@body))) ; Recuerda hacerlo!

(macroexpand '(A menos que sea verdad(reversa "Hola Mundo")))
;; ->
;; (if (clojure.core/no verdadero) (hacer (reversa "Hola Mundo")))

;; (a menos) evaluar y regresar este cuerpo si el primer argumento es falso.
;; Otra Manera, Devolver cero

(A menos que sea verdadero "Hola") ; -> "nulo"
(A menos que sea Falso "Hola") ; -> "Hola"

;; Used without care, macros can do great evil by clobbering your vars
(defmacro define-x []
  '(do
     (def x 2)
     (list x)))

(def x 4)
(define-x) ; -> (2)
(list x) ; -> (2)

;; To avoid this, use gensym to get a unique identifier
(gensym 'x) ; -> x1281 (or some such thing)

(defmacro define-x-safely []
  (let [sym (gensym 'x)]
    `(do
       (def ~sym 2)
       (list ~sym))))

(def x 4)
(define-x-safely) ; -> (2)
(list x) ; -> (4)

;; You can use # within ` to produce a gensym for each symbol automatically
(defmacro define-x-hygenically []
  `(do
     (def x# 2)
     (list x#)))

(def x 4)
(define-x-hygenically) ; -> (2)
(list x) ; -> (4)

;; It's typical to use helper functions with macros. Let's create a few to
;; help us support a (dumb) inline arithmetic syntax
(declare inline-2-helper)
(defn clean-arg [arg]
  (if (seq? arg)
    (inline-2-helper arg)
    arg))

(defn apply-arg
  "Given args [x (+ y)], return (+ x y)"
  [val [op arg]]
  (list op val (clean-arg arg)))

(defn inline-2-helper
  [[arg1 & ops-and-args]]
  (let [ops (partition 2 ops-and-args)]
    (reduce apply-arg (clean-arg arg1) ops)))

;; We can test it immediately, without creating a macro
(inline-2-helper '(a + (b - 2) - (c * 5))) ; -> (- (+ a (- b 2)) (* c 5))

; However, we'll need to make it a macro if we want it to be run at compile time
(defmacro inline-2 [form]
  (inline-2-helper form)))

(macroexpand '(inline-2 (1 + (3 / 2) - (1 / 2) + 1)))
; -> (+ (- (+ 1 (/ 3 2)) (/ 1 2)) 1)

(inline-2 (1 + (3 / 2) - (1 / 2) + 1))
; -> 3 (actually, 3N, since the number got cast to a rational fraction with /)

