# from fastapi import APIRouter, Depends, HTTPException
# from sqlalchemy.orm import Session
# from db.models import Item
# from db.session import SessionLocal
# from db.schemas import ItemCreate, ItemResponse

# router = APIRouter()

# # Dependencia para obtener una sesión de base de datos
# def get_db():
#     db = SessionLocal()
#     try:
#         yield db
#     finally:
#         db.close()

# @router.post("/items/", response_model=ItemResponse)
# def create_item(item: ItemCreate, db: Session = Depends(get_db)):
#     db_item = Item(name=item.name, description=item.description)
#     db.add(db_item)
#     db.commit()
#     db.refresh(db_item)
#     return db_item

# @router.get("/items/{item_id}", response_model=ItemResponse)
# def read_item(item_id: int, db: Session = Depends(get_db)):
#     item = db.query(Item).filter(Item.id == item_id).first()
#     if item is None:
#         raise HTTPException(status_code=404, detail="Item not found")
#     return item
