class CreatePlayers < ActiveRecord::Migration
  def change
    create_table :players do |t|
      t.string :name
      t.boolean :is_playing
      t.text :map

      t.timestamps null: false
    end
  end
end
