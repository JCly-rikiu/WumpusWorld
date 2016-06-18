class StarterController < ApplicationController

  include Map_data

  def init
    render json: { status: 0, data: get_positions }
  end

  def resume
    if Player.exists?(name: params[:nickname])
      render json: { status: 0 }
    else
      render json: { status: 1 }
    end
  end

  def start
    nickname = params[:nickname]
    if Player.exists?(name: nickname)
      render json: { status: 0 }
    else
      Player.create(name: nickname, is_playing: true, map: create_new_game)
      render json: { status: 0 }
    end
  end

  private

  def create_new_game
    ''
  end
end
